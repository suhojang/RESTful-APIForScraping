package com.kwic.web.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.exception.DefinedException;
import com.kwic.util.DateFormat;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;
import com.kwic.web.validator.ValidatorRules;
import com.kwic.xml.parser.JXParser;

public class ParamValidator extends DefaultInterceptor{

	public static final Map<String,String> paramValidatorRegExMap	= new HashMap<String,String>();
	static {
		paramValidatorRegExMap.put("en"			,"^[a-zA-Z]*$" );
		paramValidatorRegExMap.put("enS"		,"^[a-z]*$" );
		paramValidatorRegExMap.put("enB"		,"^[A-Z]*$" );
		paramValidatorRegExMap.put("numEn"		,"^[a-zA-Z0-9]*$" );
		paramValidatorRegExMap.put("plong"		,"^\\d*$" );
		paramValidatorRegExMap.put("nlong"		,"^[\\-]{1}\\d*$" );
		paramValidatorRegExMap.put("long"		,"^[\\-]{0,1}\\d*$" );
		paramValidatorRegExMap.put("pdouble"	,"^\\d*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("ndouble"	,"^[\\-]{1}\\d*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("double"		,"^[\\-]{0,1}\\d*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("pcurrency"	,"^[0-9\\,]*$" );
		paramValidatorRegExMap.put("ncurrency"	,"^[\\-]{1}[0-9\\,]*$" );
		paramValidatorRegExMap.put("currency"	,"^[\\-]{0,1}[0-9\\,]*$" );
		paramValidatorRegExMap.put("pdblcurrency"	,"^[0-9\\,]*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("ndblcurrency"	,"^[\\-]{1}[0-9\\,]*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("dblcurrency"	,"^[\\-]{0,1}[0-9\\,]*[\\.]{0,1}\\d*$" );
		paramValidatorRegExMap.put("email1"		,"^[a-zA-Z]{1,}[0-9a-zA-Z\\_\\-\\.]*$" );
		paramValidatorRegExMap.put("email2"		,"^[a-z]{1,}[0-9a-zA-Z\\_\\-]{1,}\\.{1}[0-9a-zA-Z\\_\\-]{1,}\\.{0,1}[0-9a-zA-Z\\_\\-]*$" );
		paramValidatorRegExMap.put("email"		,"^[0-9a-zA-Z\\_\\-\\.]{1,}@[0-9a-zA-Z\\_\\-]{1,}\\.{1}[0-9a-zA-Z\\_\\-]{1,}\\.{0,1}[0-9a-zA-Z\\_\\-]*$" );
		paramValidatorRegExMap.put("phone1"		,"^\\d{2,4}$" );
		paramValidatorRegExMap.put("phone2"		,"^\\d{3,4}$" );
		paramValidatorRegExMap.put("phone3"		,"^\\d{4}$" );
		paramValidatorRegExMap.put("phone"		,"^\\d{2,4}[\\-]{0,1}\\d{3,4}[\\-]{0,1}\\d{4}$" );
		paramValidatorRegExMap.put("mobile1"	,"^01(0|1|[6-9])$" );
		paramValidatorRegExMap.put("mobile2"	,"^\\d{3,4}$" );
		paramValidatorRegExMap.put("mobile3"	,"^\\d{4}$" );
		paramValidatorRegExMap.put("mobile"		,"^01(0|1|[6-9])[\\-]{0,1}\\d{3,4}[\\-]{0,1}\\d{4}$" );
	}
	public static final Map<String,String> paramValidatorNameNameMap	= new HashMap<String,String>();
	static {
		paramValidatorNameNameMap.put("en"				, "?????????" );
		paramValidatorNameNameMap.put("enS"				, "?????????" );
		paramValidatorNameNameMap.put("enB"				, "?????????" );
		paramValidatorNameNameMap.put("numEn"			, "??????/??????" );
		paramValidatorNameNameMap.put("plong"			, "?????? ??????" );
		paramValidatorNameNameMap.put("nlong"			, "?????? ??????" );
		paramValidatorNameNameMap.put("long"			, "??????" );
		paramValidatorNameNameMap.put("pdouble"			, "?????? ??????" );
		paramValidatorNameNameMap.put("ndouble"			, "?????? ??????" );
		paramValidatorNameNameMap.put("double"			, "??????/??????" );
		paramValidatorNameNameMap.put("pcurrency"		, "?????? ??????" );
		paramValidatorNameNameMap.put("ncurrency"		, "?????? ??????" );
		paramValidatorNameNameMap.put("currency"		, "?????? ??????" );
		paramValidatorNameNameMap.put("pdblcurrency"	, "?????? ??????(????????????)" );
		paramValidatorNameNameMap.put("ndblcurrency"	, "?????? ??????(????????????)" );
		paramValidatorNameNameMap.put("dblcurrency"		, "??????(????????????)" );
		paramValidatorNameNameMap.put("email1"			, "?????????" );
		paramValidatorNameNameMap.put("email2"			, "?????????" );
		paramValidatorNameNameMap.put("email"			, "?????????" );
		paramValidatorNameNameMap.put("phone1"			, "????????????" );
		paramValidatorNameNameMap.put("phone2"			, "????????????" );
		paramValidatorNameNameMap.put("phone3"			, "????????????" );
		paramValidatorNameNameMap.put("phone"			, "????????????" );
		paramValidatorNameNameMap.put("mobile1"			, "??????????????????" );
		paramValidatorNameNameMap.put("mobile2"			, "??????????????????" );
		paramValidatorNameNameMap.put("mobile3"			, "??????????????????" );
		paramValidatorNameNameMap.put("mobile"			, "??????????????????" );
		paramValidatorNameNameMap.put("date"			, "??????" );
		paramValidatorNameNameMap.put("time"			, "??????" );
		paramValidatorNameNameMap.put("datetime"		, "??????" );
		paramValidatorNameNameMap.put("ascii"			, "??????/??????/????????????" );
	}
	
	private boolean validateParams;
	private boolean blockUndefinedAction;
	
	public void setValidateParams(String validateParams){
		this.validateParams	= Boolean.parseBoolean(validateParams);
	}
	public void setBlockUndefinedAction(String blockUndefinedAction){
		this.blockUndefinedAction	= Boolean.parseBoolean(blockUndefinedAction);
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	if(!validateParams)
    		return true;
    	
    	String uri	= getRequestURI(request);
    	
    	if(!isContainedUri(uri))
    		return true;

    	String message	= null;
    	try{
	    	//parameter validation
    		validateParams(request);
    	}catch(Exception e){
    		message	= e.getMessage();
    		logger.error(e);
    	}
    	if(message==null)
    		return true;
    	//???????????????????????? ?????? ?????? ??????
    	int uriPattern	= getUriPattern(uri);
		
    	if(uriPattern==URI_PATTERN_AJAX){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD, "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_IVALIDPARAM);
			obj.put(Controllers.RESULT_MSG, message);
    			
	    	String jsonString = null;
	    	try{
	    		jsonString	= new ObjectMapper().writeValueAsString(obj);
		    	String callback	= request.getParameter("callback");
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
				response.getWriter().close();
	    	}catch(Exception e){
	    		logger.error(e);
	    	}
	    	return false;
    	}else{
    		ModelAndView modelAndView	= new ModelAndView();
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception(message));
    		modelAndView.addObject(Controllers.REQUEST_ERROR_MSG, message);
    		request.setAttribute(Controllers.REQUEST_ERROR_MSG, message);
    		throw new ModelAndViewDefiningException(modelAndView);
    	}
    }
	
	private void validateParams(HttpServletRequest request) throws Exception {
		String uri			= getRequestURI(request);
		if("".equals(uri))
			return;
		
		JXParser xmlValidator	= ValidatorRules.getInstance().getValidator(uri);
		if(blockUndefinedAction && xmlValidator==null) {
			throw new DefinedException("URI ["+uri+"]??? ?????? parameter validator??? ???????????? ???????????????.");
		}else if(xmlValidator==null) {
			logger.debug("Verification definition not found. uri=["+uri+"]");
			return;
		}
			
		Element action		= xmlValidator.getElement("//action[@uri='"+uri+"']");
		if(blockUndefinedAction && action==null)
			throw new DefinedException("URI ["+uri+"]??? ?????? parameter validator??? ???????????? ???????????????.");
		else if(action==null)
			return;
		boolean blockUndefined	= (xmlValidator.getAttribute(action, "blockUndefined")==null || "".equals(xmlValidator.getAttribute(action, "blockUndefined").trim()))?false:Boolean.parseBoolean(xmlValidator.getAttribute(action, "blockUndefined"));
		if(blockUndefined) {
			Enumeration<String> pNames	= request.getParameterNames();
			String pName	= null;
			while(pNames.hasMoreElements()) {
				pName	= pNames.nextElement();
				if(pName.toLowerCase().startsWith("callback"))
					continue;
				if(pName.equals(RequestTokenInterceptor.ATTRIBUTE_NAME))
					continue;
				if(xmlValidator.getElement(action,"param[@id='"+pName+"']")==null)
					throw new DefinedException("???????????? ?????? ???????????? ["+pName+"]??? ?????????????????????.");
			}
		}
		String encoding		= xmlValidator.getAttribute(action, "encoding");
		if(encoding==null || "".equals(encoding.trim()))
			encoding	= "UTF-8";
		
		Element[] params	= xmlValidator.getElements(action,"param");
		
		String _id			= null;
		String _type		= null;
		String _typeName	= null;
		String _desc		= null;
		String _val			= null;
		int _minBytes		= 0;
		int _maxBytes		= 0;
		int _fixBytes		= 0;
		String _dateFormat	= null;
		String _rexp		= null;
		boolean _required	= false;
		boolean _result		= true;

		for(int i=0;i<params.length;i++) {
			_id			= xmlValidator.getAttribute(params[i], "id");
			_type		= xmlValidator.getAttribute(params[i], "type");
			_typeName	= paramValidatorNameNameMap.get(_type);
			_typeName	= (xmlValidator.getAttribute(params[i], "typeName")==null||"".equals(xmlValidator.getAttribute(params[i], "typeName")))?_typeName:xmlValidator.getAttribute(params[i], "typeName");
			_desc		= xmlValidator.getAttribute(params[i], "desc");
			_desc		= (_desc==null||"".equals(_desc))?_id:_desc;
			_val		= request.getParameter(_id);
			_dateFormat		= xmlValidator.getAttribute(params[i], "dateFormat");
			_rexp		= xmlValidator.getAttribute(params[i], "rexp");
			if(xmlValidator.getAttribute(params[i], "encoding")!=null && !"".equals(xmlValidator.getAttribute(params[i], "encoding")))
				encoding	= xmlValidator.getAttribute(params[i], "encoding");
			_minBytes	= (xmlValidator.getAttribute(params[i], "minBytes")==null||"".equals(xmlValidator.getAttribute(params[i], "minBytes")))?-1:Integer.parseInt(xmlValidator.getAttribute(params[i], "minBytes"));
			_maxBytes	= (xmlValidator.getAttribute(params[i], "maxBytes")==null||"".equals(xmlValidator.getAttribute(params[i], "maxBytes")))?-1:Integer.parseInt(xmlValidator.getAttribute(params[i], "maxBytes"));
			_fixBytes	= (xmlValidator.getAttribute(params[i], "fixBytes")==null||"".equals(xmlValidator.getAttribute(params[i], "fixBytes")))?-1:Integer.parseInt(xmlValidator.getAttribute(params[i], "fixBytes"));
			_required	= xmlValidator.getAttribute(params[i], "required")==null?false:Boolean.parseBoolean(xmlValidator.getAttribute(params[i], "required"));
			
			//is required
			if(_required && (_val==null||"".equals(_val)))
				throw new DefinedException("["+_desc+"] ????????? ?????????????????????.");
			
			if(_val==null)
				_val	= "";
			
			switch(_type){
				case "any" :
					break;
				case "en" :
				case "enS" :
				case "enB" :
				case "numEn" :
				case "plong" :
				case "nlong" :
				case "long" :
				case "pdouble" :
				case "ndouble" :
				case "double" :
				case "pcurrency" :
				case "ncurrency" :
				case "currency" :
				case "pdblcurrency" :
				case "ndblcurrency" :
				case "dblcurrency" :
				case "email1" :
				case "email2" :
				case "email" :
				case "phone1" :
				case "phone2" :
				case "phone3" :
				case "phone" :
				case "mobile1" :
				case "mobile2" :
				case "mobile3" :
				case "mobile" :
					_result	= _val.matches(paramValidatorRegExMap.get(_type));
					break;
				case "date" :
				case "time" :
				case "datetime" :
					_val	= StringUtil.remove(StringUtil.remove(StringUtil.remove(StringUtil.remove(StringUtil.remove(_val, "-"),"."),"/")," "),":");
					if(!_val.matches("^\\d{2,17}$")){
						_result	= false;
						break;
					}
					if(_dateFormat!=null && !"".equals(_dateFormat)) {
						_result		= DateFormat.isValidDate(_val,_dateFormat);
						_typeName	= _typeName+" ( "+_dateFormat+" ) ";
					}
					break;
				case "ascii" :
					for(int j=0;j<_val.length();j++){
						if(_val.charAt(j)>127){
							_result	= false;
							break;
						}
					}
					break;
				case "user" :
					if(_rexp==null) {
						_result	= true;
						break;
					}
					if(_rexp.trim().startsWith("/"))
						_rexp	= _rexp.trim().substring(1);
					if(_rexp.trim().endsWith("/"))
						_rexp	= _rexp.trim().substring(0,_rexp.length()-2);
					_result		= _val.matches(_rexp);
					break;
			}
			if(!_result)
				throw new DefinedException("["+_desc+"] ????????? ["+_typeName+"] ????????? ???????????????.");
			
			//min bytes length
			if(_minBytes>0 && _val.getBytes(encoding).length<_minBytes)
				throw new DefinedException("["+_desc+"] ????????? ????????? ???????????? ["+_minBytes+" Bytes] ?????? ????????????.");

			//max bytes length
			if(_maxBytes>0 && _val.getBytes(encoding).length>_maxBytes)
				throw new DefinedException("["+_desc+"] ????????? ????????? ???????????? ["+_minBytes+" Bytes]??? ?????????????????????.");
			
			//fixed bytes length
			if(_fixBytes>0 && _val.getBytes(encoding).length!=_fixBytes)
				throw new DefinedException("["+_desc+"] ????????? ????????? ???????????? ["+_minBytes+" Bytes]??? ????????????.");
		}
	}
}
