	var fn_tgrid_load_sheet_data = (function(a_data) {
		/* show grid */
		//--g_el_tgrid.style.display = 'block';
		fn_tgrid_win_resize();

		/* set up table headers */
		var L = 0;
		a_data.forEach(function(r) { if(L < r.length) L = r.length; });
	  //--@@console.log(L);
		if (a_data.length > 0) {
      for (var n_ii = a_data[0].length; n_ii < L; n_ii++) {
        a_data[0][n_ii] = '';
      }
		}

		/* load data */
		g_o_canvas_datagrid.data = a_data;
	});

	var g_el_tgrid, g_o_canvas_datagrid;

	function fn_tgrid_init_grid() {
		g_el_tgrid = document.getElementById('id_canvas_datagrid');

		var c_grid_Font = '12px NanumGothic,sans-serif';
		g_o_canvas_datagrid = canvasDatagrid({
			parentNode: g_el_tgrid,

			//--allowColumnReordering: true,
			allowSorting: false,
			autoResizeColumns: true,
			columnHeaderClickBehavior: 'select',  //-- sort, select, none
			editable: false,
			multiLine: true,
			//--selectionMode: 'cell',  //-- cell, row

			showClearSettingsOption: false,
			//--showColumnHeaders: true,
			//--showColumnSelector: true,
			//--showCopy: true,
			showFilter: false,
			//--showNewRow: true,
			//--showOrderByOption: true,
			//--showOrderByOptionTextAsc: 'Order by %s desc',
			//--showOrderByOptionTextDesc: 'Order by %s ascending',
			//--showPaste: false,
			//--showPerformance: false,
			//--showRowHeaders: true,
			//--showRowNumbers: true,
					style: {
						cellFont: c_grid_Font,
						activeCellFont: c_grid_Font,
						columnHeaderCellFont: c_grid_Font,
						contextMenuChildArrowFontSize: c_grid_Font,
						contextMenuFontSize: c_grid_Font,
						editCellFontSize: c_grid_Font,
						rowHeaderCellFont: c_grid_Font,
					}
			});
		g_o_canvas_datagrid.style.width = '100%';
		g_o_canvas_datagrid.style.height = '600px';
	}

	function fn_tgrid_win_resize() {
		//--##g_el_tgrid.style.height = (window.innerHeight - 20) + 'px';
		//--##g_el_tgrid.style.width = (window.innerWidth - 20 - 20) + 'px';
	}

	$(document).ready(function() {
		fn_tgrid_init_grid();
		window.addEventListener('resize', fn_tgrid_win_resize);
	});
