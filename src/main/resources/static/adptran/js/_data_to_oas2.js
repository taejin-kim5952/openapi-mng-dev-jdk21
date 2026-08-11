
	//--### build swagger object function (ref: pathReqForm.jsp) {
	//-- request header
	function fn_get_req_header_parameters(a_data) {
		var arr_root_node = convertToHierarchy(a_data);
		//--@@console.log('[o-o][req header hierarchy][%o]', arr_root_node);

		var paramArray = [];

		for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
			var data = arr_root_node[n_ii];
			var name = data.node['name'];
			var datatype = data.node['datatype'];
			var desc = data.node['desc'];
			var required = data.node['required'];
			var example = ' ';
			//-- ext {
      var x_required		 = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue	 = data.node['default'];
      var x_hidden			 = data.node['cp'];
      var x_bigo				 = data.node['etc'];
			//-- ext }

			var paramOb = {};
			paramOb['in'] = 'header';
			paramOb['name'] = name;
			paramOb['description'] = desc;
			paramOb['required'] = (required == 'Y');
			paramOb['x-example'] = example;
			paramOb['x-dataTypeCd'] = 'PRMTYP1010';		// PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
			//-- ext {
			paramOb['x-required']		 = x_required;
      paramOb['x-personalData'] = x_personalData;
      paramOb['x-fixedValue']	 = x_fixedValue;
      paramOb['x-hidden']			 = x_hidden;
      paramOb['x-bigo']				 = x_bigo;
			//-- ext }

			if (datatype == 'Array') {
				var emptyOb = {};
				jsp_typeArrayFn(data, emptyOb);
				paramOb['type']  = emptyOb[name]['type'];
				paramOb['items'] = emptyOb[name]['items'];
			}
			else {
				paramOb['type'] = datatype.toLowerCase();
			}
			paramArray.push(paramOb);
		}
		//--@@console.log('[o-o][req header parameters][%o]', paramArray);

		return paramArray;
	}

	//-- request body		
	function fn_get_req_body_parameter(a_data) {
		var arr_root_node = convertToHierarchy(a_data);
		//--@@console.log('[o-o][req body hierarchy][%o]', arr_root_node);

		var paramOb = {};
		if (arr_root_node.length > 0) {
  		var dataOb = {};
  
  		exampleOb = {};
  		var description = '';

  		paramOb['in'] = 'body';
  		paramOb['name'] = 'body';  //--@[초기값설정][overwrite됨?]
  		paramOb['description'] = description;
  		paramOb['schema'] = {};
  		paramOb['x-dataTypeCd'] = 'PRMTYP1010';	// PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

  		var data = arr_root_node[0];
  		var name = data.node['name'];
  		var datatype = data.node['datatype'];
  		var desc = data.node['desc'];
  		var required = data.node['required'];
  		var example = ' ';
  		//-- ext {
      var x_required		 = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue	 = data.node['default'];
      var x_hidden			 = data.node['cp'];
      var x_bigo				 = data.node['etc'];
  		//-- ext }
  
  		//-- ext {
  		paramOb['x-required']		 = x_required;
      paramOb['x-personalData'] = x_personalData;
      paramOb['x-fixedValue']	 = x_fixedValue;
      paramOb['x-hidden']			 = x_hidden;
      paramOb['x-bigo']				 = x_bigo;
  		//-- ext }
  		
  		if (datatype == 'Array') {
  			paramOb['name'] = name;
  			paramOb['required'] = (required == 'Y');
  			paramOb['schema']['type'] = datatype.toLowerCase();
  			paramOb['x-example'] = example;
  
  			dataOb[0] = data
  			jsp_typeArrayFn(dataOb[0], emptyOb);
  			paramOb['schema']['items']  = emptyOb[name]['items'];
  			paramOb['schema']['description']  = desc;
  		}
  		else if (datatype == 'Object') {
  			dataOb[0] = data;
  			if (data.children.length > 0) {		//-- ?? maybe always
  				var emptyOb = {};
  				jsp_typeObject(dataOb[0], emptyOb);
  				paramOb['schema']['properties'] = emptyOb['properties'][name]['properties'];
  				paramOb['schema']['required']   = emptyOb['properties'][name]['required'];
  			}
  			paramOb['name'] = name;
  			paramOb['required'] = (required == 'Y');
  			paramOb['schema']['type'] = datatype.toLowerCase();
  			paramOb['schema']['description'] = desc;
  			paramOb['x-example'] = ('' + JSON.stringify(exampleOb) + '');
  		}
  		else {
  			paramOb['name'] = name;
  			paramOb['required'] = (required == 'Y');
  			paramOb['x-example'] = example;
  			if (datatype.indexOf('(data type)') != -1) {	//-- not in case
  				paramOb['schema']['$ref'] = '#/definitions/' + datatype;
  				paramOb['x-dataTypeCd'] = 'PRMTYP1040';
  			}
  			else {
  				paramOb['schema']['type'] = datatype.toLowerCase();
  				paramOb['schema']['description'] = desc;;
  			}
  		}
		}
		//--@@console.log('[o-o][req body parameter][%o]', paramOb);

		return paramOb;
	}

	//-- response header
	function fn_get_res_header_headers(a_data) {
		var arr_root_node = convertToHierarchy(a_data);
		//--@@console.log('[o-o][res header hierarchy][%o]', arr_root_node);

		var o_headers = {};

		for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
			var data = arr_root_node[n_ii];
			var name = data.node['name'];
			var datatype = data.node['datatype'];
			var desc = data.node['desc'];
			var required = data.node['required'];
			var example = ' ';

  		//-- ext {
      var x_required		 = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue	 = data.node['default'];
      var x_hidden			 = data.node['cp'];
      var x_bigo				 = data.node['etc'];
  		//-- ext }
  
			var paramOb = {};
			paramOb = {};
			paramOb['description'] = desc;
			paramOb['x-example'] = example;
			paramOb['x-dataTypeCd'] = 'PRMTYP1020';	// PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
  		//-- ext {
  		paramOb['x-required']		 = x_required;
      paramOb['x-personalData'] = x_personalData;
      paramOb['x-fixedValue']	 = x_fixedValue;
      paramOb['x-hidden']			 = x_hidden;
      paramOb['x-bigo']				 = x_bigo;
  		//-- ext }
			
			if (datatype == 'Array') {
				var emptyOb = {};
				jsp_typeArrayFn(data, emptyOb);
				paramOb['type']  = emptyOb[name]['type'];
				paramOb['items'] = emptyOb[name]['items'];
			}
			else {
				paramOb['type'] = datatype.toLowerCase();
			}
			o_headers[name] = paramOb;
		}
		//--@@console.log('[o-o][res header headers][%o]', o_headers);

		return o_headers;
	}

	//-- response body
	function fn_get_res_body_schema(a_data) {
		var arr_root_node = convertToHierarchy(a_data);
		//--@@console.log('[o-o][res body hierarchy][%o]', arr_root_node);

		var dataOb = {};

		if (arr_root_node.length > 0) {
  		//-- just 1 parameter
  		//--##for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
  		exampleOb = {};
  		var description = '';
  		
  		var paramOb = {};
  		paramOb['x-description'] = description;
  		paramOb['x-dataTypeCd'] = 'PRMTYP1020';	// PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
		
			var data = arr_root_node[0];
			var name = data.node['name'];
			var datatype = data.node['datatype'];
			var desc = data.node['desc'];
			var required = data.node['required'];
			var example = ' ';
			//-- ext {
      var x_required		 = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue	 = data.node['default'];
      var x_hidden			 = data.node['cp'];
      var x_bigo				 = data.node['etc'];
			//-- ext }

			//-- ext {
			paramOb['x-required']		 = x_required;
      paramOb['x-personalData'] = x_personalData;
      paramOb['x-fixedValue']	 = x_fixedValue;
      paramOb['x-hidden']			 = x_hidden;
      paramOb['x-bigo']				 = x_bigo;
			//-- ext }

			if (datatype == 'Array') {
				dataOb[0] = data;
				var emptyOb = {};
				jsp_typeArrayFn(dataOb[0], emptyOb);

				paramOb['type'] = datatype.toLowerCase();
				paramOb['items'] = emptyOb[name]['items'];
				paramOb['description']  = desc;
				paramOb['x-name'] = name;
				paramOb['example'] = ('' + example + '');
			}
			else if (datatype == 'Object') {
				dataOb[0] = data;
				if (data.children.length > 0) {		//-- ?? maybe always
					var emptyOb = {};
					jsp_typeObject(dataOb[0], emptyOb);
					paramOb = emptyOb;
				}
				paramOb['type'] = datatype.toLowerCase();
				paramOb['description'] = desc;
				paramOb['x-name'] = name;
				paramOb['example'] = ('' + JSON.stringify(exampleOb) + '');
			}
			else {
				paramOb['example'] = example;
				paramOb['description'] = desc;
				paramOb['x-name'] = name;
				if (datatype.indexOf('(data type)') != -1) {	//-- not in case
					paramOb['schema']['$ref'] = '#/definitions/' + datatype;
					paramOb['x-dataTypeCd'] = 'PRMTYP1040';
				}
				else {
					paramOb['type'] = datatype.toLowerCase();
				}
			}
			//--##}
		}	//-- if (arr_root_node.length > 0) {
		//--@@console.log('[o-o][res body schema][%o]', paramOb);

		return paramOb;
	}

	var exampleOb = {};
	var exampleArrayStr;

	function jsp_typeObject(data, object) {
		//-- custom logic {
		var name = 'dummy_root';
		var desc = 'dummy root';
		var datatype = 'Object';

		data = getNodeObject(makeNode(name, datatype), [data], null);	//-- 가상 root를 작성
		//-- custom logic }

		object['properties'] = {};
		object['required'] = [];

		//-- org코드의 구조는 실상은 1번만 호출되는 내용이 되는듯

		exampleOb = {};
		for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
			jsp_typeObjectTwo(
				data.children[n_ii],
				object['properties'],
				exampleOb,
				object['required']
			);
		}
	}

	/*--
		{
			node: { id, pid, name, datatype, desc, required, size, default, etc, pv_data, cp },
			children:[],
			parent
		}
	--*/
	function jsp_typeObjectTwo(data, object, exOb, requriedArray) {
		var name = data.node['name'];
		var datatype = data.node['datatype'];
		var desc = data.node['desc'];
		var required = data.node['required'];
		var example = ' ';
		//-- ext {
    var x_required		 = data.node['required'];
    var x_personalData = data.node['pv_data'];
    var x_fixedValue	 = data.node['default'];
    var x_hidden			 = data.node['cp'];
    var x_bigo				 = data.node['etc'];
		//-- ext }

		object[name] = (object[name]||{});
    //-- ext {
		object[name]['x-required']		 = x_required;
    object[name]['x-personalData'] = x_personalData;
    object[name]['x-fixedValue']	 = x_fixedValue;
    object[name]['x-hidden']			 = x_hidden;
    object[name]['x-bigo']				 = x_bigo;
		//-- ext }

		if (datatype == 'Object') {
			object[name] = {};
			object[name]['type'] = datatype.toLowerCase();
			object[name]['description'] = desc;
			object[name]['properties'] = {};
			if (example != undefined) {
				object[name]['x-example'] = example;
			}
			if (required == 'Y') {
				requriedArray.push(name);
			}
			exOb[name] = {};

			object[name]['required'] = [];
			for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
				jsp_typeObjectTwo(
					data.children[n_ii],
					object[name]['properties'],
					exOb[name],
					object[name]['required']
				);
			}
		}
		else if (datatype == 'Array') {
			exampleArrayStr = '';
			jsp_typeArrayFn(data, object);
			object[name]['description'] = desc;
			if (example != undefined) {
				object[name]['x-example'] = example;
			}
			if (required == 'Y') {
				requriedArray.push(name);
			}
			exOb[name] = exampleArrayStr;
		}
		else {
			object[name] = {};
			exOb[name] = example;
			if (datatype.indexOf('(data type)') != -1) {	//-- not in case
				object[name]['$ref'] = '#/definitions/' + datatype;
				paramOb['x-dataTypeCd'] = 'PRMTYP1040';
			}
			else {
				if (required == 'Y') {
					requriedArray.push(name);
				}
				object[name]['type'] = datatype.toLowerCase();
				object[name]['description'] = desc;
				if (example != undefined) {
					object[name]['x-example'] = example;
				}
			}
		}
		return object;
	}

	function jsp_typeArrayFn(data, object){
		var name = data.node['name'];
		var datatype = data.node['datatype'];
		var desc = data.node['desc'];
		var required = data.node['required'];
		var example = ' ';

		if (example != '') {
			exampleArrayStr = example;
		}
		
		//--##var is_parent_array_root = ((data.parent != null) ? ((data.parent.node['datatype'] == 'Array') && (data.parent.children.length == 1)) : false);
		//--##if (is_parent_array_root == false) {	//-- case가 없어보임
		if (name == '') {	//-- (name == '') case가 없어보임
			object['items'] = {};
			typeArray = object['items'];
		}
		else {
			object[name] = {};
			typeArray = object[name];
		}
		
		while (data.children.length > 0) {
			name = data.node['name'];
			datatype = data.node['datatype'];
			desc = data.node['desc'];
			required = data.node['required'];
  		//-- ext {
      var x_required		 = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue	 = data.node['default'];
      var x_hidden			 = data.node['cp'];
      var x_bigo				 = data.node['etc'];
  		//-- ext }
      //-- ext {
      typeArray['x-required']		 = x_required;
      typeArray['x-personalData'] = x_personalData;
      typeArray['x-fixedValue']	 = x_fixedValue;
      typeArray['x-hidden']			 = x_hidden;
      typeArray['x-bigo']				 = x_bigo;
      //-- ext }

			if (datatype == 'Array') {
				type = datatype.toLowerCase();
				example = ' ';
				typeArray = jsp_typeArrayMake(type, example, typeArray);
				//-- custom logic {
				if (data.children.length > 1) {
					data.node['datatype'] = 'Object';	//-- change to object node  아래의 data = data.child[0]에 의
				}
				else {
					data = data.children[0];
				}
				//-- custom logic }
        //-- ext {
        typeArray['x-required']		 = x_required;
        typeArray['x-personalData'] = x_personalData;
        typeArray['x-fixedValue']	 = x_fixedValue;
        typeArray['x-hidden']			 = x_hidden;
        typeArray['x-bigo']				 = x_bigo;
        //-- ext }
			}
			else if (datatype == 'Object') {
				typeArray['type'] = datatype.toLowerCase();
				if (example != undefined) {
					typeArray['x-example'] = example;
				}
				typeArray['properties'] = {};
				typeArray['required'] = [];
				for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
					jsp_typeObjectTwo(
						data,
						typeArray['properties'],
						exampleOb,
						typeArray['required']
					);
				}
				break;
			}
			else {
				typeArray['type'] = datatype.toLowerCase();
				break;
			}
		}
	}

	function jsp_typeArrayMake(type, example, typeArray) {
		typeArray['type'] = type.toLowerCase();
		typeArray['items'] = {};
		return typeArray['items'];
	}
	//--### build swagger object function (ref: pathReqForm.jsp) {

	//-- node function {
	function makeNode(name, datatype) {
		return { 'id':-99, 'pid':-99, 'name': name, 'datatype': datatype, };
	}

	function getNodeObject(node, children, parent) {
		return { 'node': node, 'children': children, 'parent': parent };
	}
	//-- node function }

	//-- tree function {
	function convertToHierarchy(arry) {
		//-- method {
		var createStructure = (function(nodes) {
			var objects = [];
			for (var n_ii = 0; n_ii < nodes.length; n_ii++) { objects.push({ 'node': nodes[n_ii], 'children': [], 'parent': null }); }
			return objects;
		});
		var getParent = (function(child, nodes) {
			for (var n_ii = 0; n_ii < nodes.length; n_ii++) { if (nodes[n_ii].node.id == child.node.pid) { return nodes[n_ii]; } }
			return null;
		});
		//-- method }

		var nodeObjects = createStructure(arry);
		for (var i = nodeObjects.length - 1; i >= 0; i--) {
			var currentNode = nodeObjects[i];
			//-- skip over root node.
			if (currentNode.node.pid == -1) { 
				continue; 
			}
			var parent = getParent(currentNode, nodeObjects);
			if (parent == null) { 
				continue; 
			}
			currentNode.parent = parent;
			parent.children.push(currentNode);
			nodeObjects.splice(i, 1);
		}
		//--What remains in nodeObjects will be the root nodes.
		return nodeObjects;
	}
	//-- tree function }
