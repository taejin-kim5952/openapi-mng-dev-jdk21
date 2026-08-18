/* ============================================================
   chatWidget.js — AI 챗봇 팝업 (API Manager 도우미)

   화면(마크업/CSS)은 이 앱이 갖고, 데이터는 챗봇 서버(openapi-chat-serve)의 HTTP API 에서
   받는다. 챗봇 서버는 다른 프로세스(127.0.0.1:18100)라 브라우저가 직접 부르지 않고,
   이 앱의 /chat 프록시(ChatProxyController)를 거친다 — 같은 도메인이 되어 CORS 가 없고,
   챗봇 포트를 외부에 열지 않아도 된다.

   답변 본문 렌더링은 **챗봇 서버의 markdown.js(ChatMD)** 를 그대로 불러다 쓴다. 이 앱에
   복사하지 않는 이유: 관리자 검수 미리보기(admin.js)와 사용자에게 보이는 답변이 **글자
   하나까지 같아야** 하기 때문이다. 복사본을 두면 한쪽만 고쳐져 조용히 어긋난다.

   퍼블_v24.0 산출물 기준. 산출물의 JS 는 같은 블록이 세 번 붙어 있었는데(다른 핸들러 본문
   안까지) 여기서는 한 벌만 둔다.
   ============================================================ */

var ChatWidget = (function () {

  /* 프록시 경로. 컨텍스트패스(/apidev)는 페이지에서 c_url 로 들어온다. */
  var BASE = (typeof c_url === 'string' ? c_url : '/') + 'chat';

  var API = {
    ask:        BASE + '/api/ask',
    support:    BASE + '/api/support',
    categories: BASE + '/api/categories',
    chunk:      BASE + '/api/docs/chunk/',
    docImage:   BASE + '/api/docs/img/',
    feedback:   BASE + '/api/feedback'
  };

  var $panel, $body, $msgs, $text;
  var loaded = false;      // 카테고리를 한 번만 받아온다
  var waiting = false;     // 답변 대기 중에는 전송을 막는다
  var pickedCategoryId = '';
  var GROUPS = [];         // /api/categories 응답. 주제 목록을 다시 그릴 때 쓴다

  /* ---------- 유틸 ---------- */

  function tpl(id) {
    var el = document.getElementById(id);
    return $(el.content.cloneNode(true));
  }

  function scrollBottom() {
    if ($body && $body.length) { $body.scrollTop($body[0].scrollHeight); }
  }

  /* 서버 오류를 사용자 말로 바꾼다. 프록시는 챗봇이 안 떠 있으면 502를 준다. */
  function errText(xhr) {
    if (xhr && xhr.status === 502) { return '챗봇 서비스에 연결하지 못했습니다. 잠시 후 다시 시도해 주세요.'; }
    if (xhr && xhr.status === 401) { return '로그인이 필요합니다. 새로고침 후 다시 시도해 주세요.'; }
    return '답변을 가져오지 못했습니다. 잠시 후 다시 시도해 주세요.';
  }

  /* ---------- 카테고리(주제) ---------- */

  function loadCategories() {
    if (loaded) { return; }
    loaded = true;
    $.ajax({ url: API.categories, type: 'GET', dataType: 'json' })
      .done(function (res) { buildTopics(res); })
      .fail(function () { loaded = false; });   // 다음에 열 때 다시 시도
  }

  function buildTopics(res) {
    var groups = (res && res.groups) || [];
    var quickIds = (res && res.quick_category_ids) || [];

    var all = [];
    $.each(groups, function (_, g) {
      $.each(g.categories || [], function (_, c) { all.push(c); });
    });

    // 자주 찾는 주제 - quick_category_ids 순서를 그대로 따른다
    var $chips = $('#chatChips').empty();
    $.each(quickIds, function (_, id) {
      var cat = null;
      $.each(all, function (_, c) { if (c.category_id === id) { cat = c; return false; } });
      if (!cat) { return; }
      $('<button type="button" class="chat_chip"></button>')
        .text(cat.name).attr('data-cat-id', cat.category_id).appendTo($chips);
    });

    // 전체 주제 목록 - 그룹(접기/펼치기) 안에 주제를 넣는다. 주제가 수십 개라 평면 목록으로
    // 두면 무엇이 무엇인지 구분이 안 된다.
    GROUPS = groups;
    renderTopicList('');

    // 주제가 하나도 없는 설치처가 있다 - 그때는 주제 UI를 통째로 감춘다(질문은 계속 된다).
    var none = all.length === 0;
    $('#chatChips, .chat_intro_sub').toggleClass('qr_hide', none);
    $('#chatTopicBtn').toggleClass('qr_hide', none);
  }

  /* 1단계: 그룹 > 주제 목록. 검색어가 있으면 걸리는 주제만 남기고 그 그룹은 펼쳐서 보여준다. */
  function renderTopicList(query) {
    var q = $.trim(query || '').toLowerCase();
    var $list = $('#chatTopicList').empty();
    var shown = 0;

    $.each(GROUPS, function (_, g) {
      var cats = $.grep(g.categories || [], function (c) {
        return !q || c.name.toLowerCase().indexOf(q) > -1;
      });
      if (cats.length === 0) { return; }
      shown += cats.length;

      var $head = $('<button type="button" class="chat_topic_group">'
        + '<span class="chat_topic_group_ico"><svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><path d="M9 6l6 6-6 6"/></svg></span>'
        + '<span class="chat_topic_group_nm"></span></button>');
      $head.find('.chat_topic_group_nm').text(g.group_name || '');
      // 검색 중에는 결과가 바로 보여야 하므로 펼친 상태로 그린다.
      if (q) { $head.addClass('is_open'); }

      var $sub = $('<div class="chat_topic_sub"></div>');
      $.each(cats, function (_, c) {
        $('<button type="button" class="chat_topic"></button>')
          .text(c.name).attr('data-cat-id', c.category_id).appendTo($sub);
      });

      $list.append($head).append($sub);
    });

    $('#chatTopicEmpty').toggleClass('qr_hide', shown > 0);
  }

  function findCategory(catId) {
    var found = null;
    $.each(GROUPS, function (_, g) {
      $.each(g.categories || [], function (_, c) {
        if (c.category_id === catId) { found = c; return false; }
      });
      return found ? false : true;
    });
    return found;
  }

  /* 2단계: 고른 주제의 예시 질문. 여기서 고르면 그대로 전송한다. */
  function renderTopicQuestions(cat) {
    $('#chatTopicHeadNm').text(cat.name);
    $('#chatTopicHead').removeClass('qr_hide');
    $('#chatTopicPop').addClass('is_step2');

    var $list = $('#chatTopicList').empty();
    $.each(cat.questions || [], function (_, q) {
      $('<button type="button" class="chat_topic"></button>')
        .text(q).attr('data-question', q).appendTo($list);
    });
    $('#chatTopicEmpty').addClass('qr_hide');
  }

  /* 1단계로 되돌린다(팝오버를 열 때도 여기서 시작한다). */
  function resetTopicPop() {
    $('#chatTopicHead').addClass('qr_hide');
    $('#chatTopicPop').removeClass('is_step2');
    $('#chatTopicSrch').val('');
    renderTopicList('');
  }

  /* 고른 주제를 화면에 표시한다. 무엇이 걸려 있는지 안 보이면 왜 답이 달라지는지 알 수 없다. */
  function setPicked(cat) {
    pickedCategoryId = cat ? cat.category_id : '';
    $('#chatPickedNm').text(cat ? cat.name : '');
    $('#chatPicked').toggleClass('qr_hide', !cat);
  }

  /* ---------- 말풍선 ---------- */

  function pushMe(question) {
    var $m = tpl('chatTplMe');
    $m.find('.chat_bubble_me').text(question);
    $msgs.append($m);
    $('#chatIntro').addClass('qr_hide');
    scrollBottom();
  }

  /* 답변 본문은 ChatMD.renderAnswer()가 이스케이프 후 정해진 태그만 붙인다.
     서버가 준 문자열을 그대로 .html()에 넣지 않는다. */
  function pushAnswer(data) {
    var $m = tpl('chatTplBot');
    var $bubble = $m.find('.chat_bubble');

    $bubble.find('.chat_txt').html(ChatMD.renderAnswer(data.answer || ''));

    var $srcs = $bubble.find('.chat_srcs').empty();
    $.each(data.source_docs || [], function (_, d) {
      var $s = tpl('chatTplSrc');
      $s.find('.chat_src_nm').text(d.title + (d.section ? ' · ' + d.section : ''));
      $s.find('.chat_src').attr('data-doc-id', d.doc_id || '');
      $srcs.append($s);
    });
    $srcs.toggleClass('qr_hide', (data.source_docs || []).length === 0);

    // 검수된 QA 로 답한 경우에만 "담당자 검수 완료"를 붙인다.
    if (data.matched_qa_id) {
      $bubble.find('.chat_ai').before(
        '<span class="chat_verified">' +
        '<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12.5l4.5 4.5L19 7"/></svg>' +
        ' 담당자 검수 완료</span>');
    }
    $bubble.attr('data-log-id', data.log_id || '');

    $msgs.append($m);
    scrollBottom();
  }

  function pushRelated(data, question) {
    var $m = tpl('chatTplBot');
    var $bubble = $m.find('.chat_bubble');

    $bubble.find('.chat_txt').html(
      ChatMD.renderAnswer(data.message || '정확히 맞는 문서를 찾지 못했습니다. 관련 있어 보이는 문서를 골라봤습니다.'));
    $bubble.find('.chat_srcs').addClass('qr_hide');

    var $docs = $('<div class="chat_docs"></div>');
    $.each(data.related_docs || [], function (_, d) {
      var $c = tpl('chatTplDoc');
      $c.find('.chat_doc_nm').text(d.title);
      $c.find('.chat_doc_sec').text(d.section || '');
      $c.find('.chat_doc').attr('data-doc-id', d.doc_id || '').attr('data-chunk-id', d.chunk_id || '');
      $docs.append($c);
    });
    $bubble.find('.chat_txt').after($docs);
    $bubble.find('.chat_foot').before(unresolvedBox());
    $bubble.attr('data-log-id', data.log_id || '').attr('data-question', question || '');

    $msgs.append($m);
    scrollBottom();
  }

  function pushUnresolved(data, question) {
    var $m = tpl('chatTplBot');
    var $bubble = $m.find('.chat_bubble');
    $bubble.find('.chat_txt').html(
      ChatMD.renderAnswer(data.message || '답변을 찾지 못했습니다.'));
    $bubble.find('.chat_srcs').addClass('qr_hide');
    $bubble.find('.chat_foot').before(unresolvedBox());
    $bubble.attr('data-log-id', data.log_id || '').attr('data-question', question || '');
    $msgs.append($m);
    scrollBottom();
  }

  function unresolvedBox() {
    return '<div class="chat_unresolved"><span>원하는 답이 없었나요?</span>' +
           '<button type="button" class="qr_pill qr_pill_outline qr_pill_sm chat_ask_owner">담당자 문의</button></div>';
  }

  function pushError(msg) {
    var $m = tpl('chatTplBot');
    $m.find('.chat_txt').html(ChatMD.renderAnswer(msg));
    $m.find('.chat_srcs, .chat_fb').addClass('qr_hide');
    $msgs.append($m);
    scrollBottom();
  }

  /* ---------- 질문 전송 ---------- */

  function send(question) {
    question = $.trim(question || '');
    if (!question || waiting) { return; }

    waiting = true;
    $('#chatSend').prop('disabled', true);
    pushMe(question);
    $text.val('');

    $.ajax({
      url: API.ask, type: 'POST', contentType: 'application/json', dataType: 'json',
      data: JSON.stringify({
        question: question,
        category_id: pickedCategoryId || null,
        channel: 'web'
      })
    })
      .done(function (res) {
        if (res.result_type === 'answer') { pushAnswer(res); }
        else if (res.result_type === 'related_docs') { pushRelated(res, question); }
        else { pushUnresolved(res, question); }
      })
      .fail(function (xhr) { pushError(errText(xhr)); })
      .always(function () {
        waiting = false;
        $('#chatSend').prop('disabled', false);
        // 고른 주제는 여기서 풀지 않는다 - 같은 주제로 이어 묻는 경우가 대부분이라,
        // 한 번 쓰고 사라지면 매번 다시 골라야 한다. 해제는 칩의 ×로 한다.
      });
  }

  /* ---------- 문서 상세 ---------- */

  /* 팝업 안이 좁아 표·도식을 못 읽으므로 화면 중앙 모달로 연다. 팝업은 열린 채로 둔다. */
  function openDoc(chunkId) {
    if (!chunkId) { return; }
    $.ajax({ url: API.chunk + encodeURIComponent(chunkId), type: 'GET', dataType: 'json' })
      .done(function (d) {
        $('#chatDocTitle').text(d.title || '');
        $('#chatDocSec').text(d.section || '');
        $('#chatDocBody').html(ChatMD.renderDoc(d.text || '')).scrollTop(0);
        $('#chatDocModal').removeClass('qr_hide');
      })
      .fail(function () { alert_message('문서를 불러오지 못했습니다.'); });
  }

  /* ---------- 열기 ---------- */

  function openPanel() {
    $panel.removeClass('qr_hide chat_min');
    loadCategories();
    scrollBottom();
  }

  /* 미리 정해둔 질문을 던지며 연다. 같은 질문을 연달아 누르면 같은 답이 쌓이기만 하므로
     직전 질문과 같으면 열기만 한다. */
  function askPreset(question) {
    question = $.trim(question || '');
    if (!question) { return; }
    openPanel();

    var $last = $msgs.find('.chat_bubble_me').last();
    if ($last.length && $.trim($last.text()) === question) { scrollBottom(); return; }

    send(question);
  }

  /* ---------- 초기화 ---------- */

  function init() {
    $panel = $('#chatPanel');
    $body  = $('#chatBody');
    $msgs  = $('#chatMsgs');
    $text  = $('#chatText');

    if ($panel.length === 0) { return; }   // 챗봇을 안 붙인 화면

    // 문서 이미지 경로를 프록시 기준으로 맞춘다(챗봇 서버 기본값은 /api/docs/img/).
    ChatMD.configure({ imageBase: API.docImage });

    /* 열기 / 닫기 / 최소화 */
    $('#qrBtnChatbot').on('click', function () { openPanel(); $text.focus(); });

    /* 필드 옆 도움말 아이콘 - 팝업을 열면서 미리 정해둔 질문을 그대로 던진다.
       "무엇을 물어야 할지" 자체가 장벽이라, 라벨 옆에서 한 번에 답까지 가게 한다.
       질문 문구는 마크업의 data-chat-ask 에 둔다 - 필드 바로 옆에 있어야 같이 관리된다. */
    $(document).on('click', '[data-chat-ask]', function (e) {
      e.preventDefault();      // 라벨 안에 있어 기본 동작(입력칸 포커스)이 따라온다
      e.stopPropagation();
      askPreset($(this).attr('data-chat-ask'));
    });
    $('#chatCloseBtn').on('click', function () { $panel.addClass('qr_hide'); });
    $('#chatMinBtn').on('click', function () { $panel.toggleClass('chat_min'); });
    $('.chat_head_txt').on('click', function () {
      if ($panel.hasClass('chat_min')) { $panel.removeClass('chat_min'); }
    });

    /* 새 대화 - 서버에 대화 상태가 없어 화면만 비우면 된다 */
    $('#chatNewBtn').on('click', function () {
      $msgs.empty();
      setPicked(null);
      $('#chatIntro').removeClass('qr_hide');
    });

    /* 주제 팝오버 - 열 때는 항상 1단계부터 */
    $('#chatTopicBtn').on('click', function (e) {
      e.stopPropagation();
      var $pop = $('#chatTopicPop').toggleClass('qr_hide');
      if (!$pop.hasClass('qr_hide')) { resetTopicPop(); $('#chatTopicSrch').focus(); }
    });
    $(document).on('click', function (e) {
      if (!$(e.target).closest('#chatTopicPop, #chatTopicBtn').length) {
        $('#chatTopicPop').addClass('qr_hide');
      }
    });
    $('#chatTopicSrch').on('input', function () { renderTopicList($(this).val()); });

    /* 그룹 머리 - 펼치고 접는다 */
    $('#chatTopicList').on('click', '.chat_topic_group', function () {
      $(this).toggleClass('is_open');
    });

    /* 1단계에서 주제를 고르면 2단계(예시 질문)로 넘어간다. 예시 질문이 없는 주제는
       더 보여줄 것이 없으므로 검색 범위만 걸고 닫는다. */
    $('#chatTopicList').on('click', '.chat_topic', function () {
      var $t = $(this);

      var question = $t.attr('data-question');
      if (question) {          // 2단계에서 고른 예시 질문 - 그대로 전송
        $('#chatTopicPop').addClass('qr_hide');
        send(question);
        return;
      }

      var cat = findCategory($t.attr('data-cat-id'));
      if (!cat) { return; }
      setPicked(cat);

      if ((cat.questions || []).length > 0) {
        renderTopicQuestions(cat);
      } else {
        $('#chatTopicPop').addClass('qr_hide');
        $text.focus();
      }
    });

    /* 2단계 -> 1단계 */
    $('#chatTopicBack').on('click', function () { resetTopicPop(); $('#chatTopicSrch').focus(); });

    /* 걸어둔 주제 해제 */
    $('#chatPickedClear').on('click', function () { setPicked(null); $text.focus(); });

    /* 인트로 칩 - 주제를 걸고 그 이름을 그대로 질문으로 보낸다(기존 동작 유지) */
    $('#chatIntro').on('click', '.chat_chip', function () {
      var cat = findCategory($(this).attr('data-cat-id'));
      if (cat) { setPicked(cat); }
      send($(this).text());
    });

    /* 맨 아래로 이동 */
    $body.on('scroll', function () {
      var far = this.scrollHeight - this.scrollTop - this.clientHeight > 80;
      $('#chatToBottom').toggleClass('qr_hide', !far);
    });
    $('#chatToBottom').on('click', scrollBottom);

    /* 전송 - Enter는 보내고 Shift+Enter는 줄바꿈 */
    $('#chatSend').on('click', function () { send($text.val()); });
    $text.on('keydown', function (e) {
      if (e.keyCode === 13 && !e.shiftKey) { e.preventDefault(); send($text.val()); }
    });

    /* 출처 배지 / 관련 문서 카드 -> 문서 상세 */
    $panel.on('click', '.chat_src, .chat_doc', function () {
      openDoc($(this).attr('data-chunk-id') || $(this).attr('data-doc-id'));
    });
    $('#chatDocClose, #chatDocClose2').on('click', function () { $('#chatDocModal').addClass('qr_hide'); });
    $('#chatDocModal').on('click', function (e) {
      if (e.target === this) { $(this).addClass('qr_hide'); }
    });

    /* 답변 피드백 - log_id로 어느 응답인지 잇는다 */
    $panel.on('click', '.chat_fb_btn', function () {
      var $btn = $(this);
      var logId = $btn.closest('.chat_bubble').attr('data-log-id');
      if (!logId) { return; }
      $btn.closest('.chat_fb').find('.chat_fb_btn').removeClass('qr_on');
      $btn.addClass('qr_on');
      $.ajax({
        url: API.feedback, type: 'POST', contentType: 'application/json',
        data: JSON.stringify({ log_id: logId, vote: $btn.attr('data-fb') })
      });
    });

    /* 담당자 문의 */
    $panel.on('click', '.chat_ask_owner', function () {
      var $btn = $(this).prop('disabled', true);
      /* /api/support 는 AskRequest 를 받는다(question 필수). 접수번호는 반드시 서버가
         만든다 - 화면에서 만들면 사용자가 본 번호와 이력의 번호가 달라진다. */
      var question = $btn.closest('.chat_bubble').attr('data-question') || '';
      if (!question) { $btn.prop('disabled', false); return; }
      $.ajax({
        url: API.support, type: 'POST', contentType: 'application/json', dataType: 'json',
        data: JSON.stringify({ question: question, channel: 'web' })
      })
        .done(function (res) {
          var msg = res.message || '담당자에게 전달했습니다.';
          if (res.ticket_id) { msg += ' (접수번호 ' + res.ticket_id + ')'; }
          $btn.closest('.chat_unresolved').html('<span>' + ChatMD.esc(msg) + '</span>');
        })
        .fail(function () { $btn.prop('disabled', false); });
    });
  }

  return { init: init, send: send, open: openPanel, ask: askPreset };
})();

$(function () { ChatWidget.init(); });
