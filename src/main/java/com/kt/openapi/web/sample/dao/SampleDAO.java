package com.kt.openapi.web.sample.dao;

import com.kt.openapi.web.sample.vo.SampleDefaultVO;
import com.kt.openapi.web.sample.vo.SampleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Sample 관련 MyBatis Mapper 인터페이스
 * [마이그레이션] EgovAbstractDAO 상속 구조를 제거하고 @Mapper 인터페이스로 전환
 */
@Mapper
public interface SampleDAO {

    /**
     * 샘플 등록
     */
    void insertSample(SampleVO vo);

    /**
     * 샘플 수정
     */
    void updateSample(SampleVO vo);

    /**
     * 샘플 삭제
     */
    void deleteSample(SampleVO vo);

    /**
     * 샘플 상세 조회
     */
    SampleVO selectSample(SampleVO vo);

    /**
     * 샘플 목록 조회
     */
    List<SampleVO> selectSampleList(SampleDefaultVO searchVO);

    /**
     * 샘플 총 갯수 조회
     */
    int selectSampleListTotCnt(SampleDefaultVO searchVO);
}
