package project.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.AGT_AGT_Service;
import project.service.CMN_CMN_Service;

/**
 * Agent 정보 Controller
 * 
 * @date 2020.07.23
 * @author 장수호
 *
 */
@RestController
public class AGT_AGT_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "AGT_AGT_Service")
	private AGT_AGT_Service agtService;
	
	/**
	 * Agent 전체 조회
	 * 
	 * - 요청
	 * {
		 "userId" : "user1",
		 "certId" : "cert1",
		 "pageNo" : "1",
		 "limit" : "10"
		}
	 *
	 * - 응답
	 * {
	     "status" : "ok",
		 "message" : "success",
		 "error": "",
		 "resultCode" : "000",
		 "resultMsg" : "",
		 "agents":[{
		  "id" : "agentId",
		  "version" : "1.0.1",
		  "os" : "windows 10",
		  "privateIP" : "192.168.0.1",
		  "publicIP" : "112.324.125.155",
		  "lastSyncTime" : "20200721150653",
		  "certs" : [{
		   "userId" : "userid1",
		   "certId" : "certid1",
		   "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		   "expire" : "2021-07-21 15:40:22"
		  },{
		   "userId" : "userId1",
		   "certId" : "certid2",
		   "certnm" : "cn=홍길동(HONG GIL DONG)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		   "expire" : "2021-07-21 15:40:22"
		  }]
		 }]
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/agent", produces="application/json; charset=UTF-8")
	public void agt_agt_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateRemoteIP(request);
			try {	
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			
			//1.validate
			LayoutFilter.getInstance().validate("agt-agt-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//Agent on/off여부 타임 설정 - 요청 정보에 power(on/off) 키 존재 시 사용
			if (requestMap.get("time")==null || "".equals(String.valueOf(requestMap.get("time"))))
				requestMap.put("time", properties.getInt("time"));
			else
				requestMap.put("time", Integer.parseInt(String.valueOf(requestMap.get("time"))));
			
			//agent정보 조회
			List<Map<String,Object>> list	= agtService.AGT_AGT_S1000J(requestMap);
			
			List<Map<String,Object>> agents		= new ArrayList<Map<String,Object>>();
			Map<String,Object> agent			= null;
			
			Map<String,Object> params			= null;
			for (int i = 0; i < list.size(); i++) {
				agent	= new HashMap<String,Object>();
				
				/********************************** Agent 정보 ***********************************/
				agent.put("id", 			String.valueOf(list.get(i).get("AGTINF_ID")));
				agent.put("version", 		String.valueOf(list.get(i).get("AGTINF_VER")));
				agent.put("os", 			String.valueOf(list.get(i).get("AGTINF_OS")));
				agent.put("priavetIP", 		String.valueOf(list.get(i).get("AGTINF_PRIP")));
				agent.put("publicIP", 		String.valueOf(list.get(i).get("AGTINF_PBIP")));
				agent.put("lastSyncTime", 	String.valueOf(list.get(i).get("AGTINF_SYNDT")));
				/**********************************& Agent 정보 &***********************************/
				
				params	= new HashMap<String,Object>();
				params.put("agentId", String.valueOf(list.get(i).get("AGTINF_ID")));
				
				//Agent별 인증서 정보 조회
				List<Map<String,Object>> certList	= agtService.AGT_AGT_S1001J(params);
				
				List<Map<String,Object>> certs		= new ArrayList<Map<String,Object>>();
				Map<String,Object> cert				= null;
				for (int j = 0; j < certList.size(); j++) {
					cert	= new HashMap<String,Object>();
					
					/********************************** 인증서 정보 ***********************************/
					cert.put("userId", 	String.valueOf(certList.get(j).get("CRTINF_USRID")));
					cert.put("certId", 	String.valueOf(certList.get(j).get("CRTINF_CRTID")));
					cert.put("certnm", 	String.valueOf(certList.get(j).get("CRTINF_CRTNM")));
					cert.put("expire", 	String.valueOf(certList.get(j).get("CRTINF_EXPIRE")));
					/**********************************& 인증서 정보 &***********************************/
					
					certs.add(cert);
				}
				//인증서 정보 담기
				agent.put("certs", certs);
				
				//Agent정보 담기
				agents.add(agent);
				
			}
			//Agent정보 전체 담기
			responseMap.put("agents", agents);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
			responseMap.put(AGT_STATUS, "ok");
			responseMap.put(AGT_MESSAGE, "success");
			responseMap.put(AGT_ERROR, "");
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
				responseMap.put(AGT_ERROR, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
				responseMap.put(AGT_ERROR, "오류가 발생하였습니다.");
			}
			responseMap.put(AGT_STATUS, "fail");
			responseMap.put(AGT_MESSAGE, "error");
		} finally {
/*			
			responseMap.put(AGT_STATUS, "ok");
			responseMap.put(AGT_MESSAGE, "success");
			responseMap.put(AGT_ERROR, "");*/
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * Agent 조회 - Agent ID로 조회
	 * 
	 * - 요청
	 * {
		 "userId" : "user1",
		 "certId" : "cert1",
		 "pageNo" : "1",
		 "limit" : "10"
		}
	 *
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		 "agents":[{
		  "id" : "agentId",
		  "version" : "1.0.1",
		  "os" : "windows 10",
		  "privateIP" : "192.168.0.1",
		  "publicIP" : "112.324.125.155",
		  "lastSyncTime" : "20200721150653",
		  "certs" : [{
		   "userId" : "userid1",
		   "certId" : "certid1",
		   "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		   "expire" : "2021-07-21 15:40:22"
		  },{
		   "userId" : "userId1",
		   "certId" : "certid2",
		   "certnm" : "cn=홍길동(HONG GIL DONG)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		   "expire" : "2021-07-21 15:40:22"
		  }]
		 }]
		}
	 *  
	 * @param requestContent
	 * @param request
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/agent/{id}", produces="application/json; charset=UTF-8")
	public void agt_agt_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateRemoteIP(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			
			//1.validate
			LayoutFilter.getInstance().validate("agt-agt-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("agentId", id);
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//Agent on/off여부 타임 설정 - 요청 정보에 power(on/off) 키 존재 시 사용
			if (requestMap.get("time")==null || "".equals(String.valueOf(requestMap.get("time"))))
				requestMap.put("time", properties.getInt("time"));
			
			//agent정보 조회
			List<Map<String,Object>> list	= agtService.AGT_AGT_S1000J(requestMap);
			
			List<Map<String,Object>> agents		= new ArrayList<Map<String,Object>>();
			Map<String,Object> agent			= null;
			List<Map<String,Object>> certs		= new ArrayList<Map<String,Object>>();
			Map<String,Object> cert				= null;
			
			Map<String,Object> params			= null;
			for (int i = 0; i < list.size(); i++) {
				agent	= new HashMap<String,Object>();
				
				/********************************** Agent 정보  ***********************************/
				agent.put("id", 			String.valueOf(list.get(i).get("AGTINF_ID")));
				agent.put("version", 		String.valueOf(list.get(i).get("AGTINF_VER")));
				agent.put("os", 			String.valueOf(list.get(i).get("AGTINF_OS")));
				agent.put("priavetIP", 		String.valueOf(list.get(i).get("AGTINF_PRIP")));
				agent.put("publicIP", 		String.valueOf(list.get(i).get("AGTINF_PBIP")));
				agent.put("lastSyncTime", 	String.valueOf(list.get(i).get("AGTINF_SYNDT")));
				/**********************************& Agent 정보  &***********************************/
				
				params	= new HashMap<String,Object>();
				params.put("agentId", String.valueOf(list.get(i).get("AGTINF_ID")));
				
				//Agent별 인증서 정보 조회
				List<Map<String,Object>> certList	= agtService.AGT_AGT_S1001J(params);
				for (int j = 0; j < certList.size(); j++) {
					cert	= new HashMap<String,Object>();
					
					/********************************** 인증서 정보 ***********************************/
					cert.put("userId", 	String.valueOf(certList.get(j).get("CRTINF_USRID")));
					cert.put("certId", 	String.valueOf(certList.get(j).get("CRTINF_CRTID")));
					cert.put("certnm", 	String.valueOf(certList.get(j).get("CRTINF_CRTNM")));
					cert.put("expire", 	String.valueOf(certList.get(j).get("CRTINF_EXPIRE")));
					/**********************************& 인증서 정보 &***********************************/
					
					certs.add(cert);
				}
				//인증서 정보 담기
				agent.put("certs", certs);
				
				//Agent정보 담기
				agents.add(agent);
				
			}
			//Agent정보 전체 담기
			responseMap.put("agents", agents);
			
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
			}
		} finally {
			responseMap.put(AGT_STATUS, "ok");
			responseMap.put(AGT_MESSAGE, "success");
			responseMap.put(AGT_ERROR, "");
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * Agent 등록
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253",
		 "id" : "agentId",
		 "version" : "1.0.1",
		 "os" : "windows 10",
		 "privateIP" : "192.168.0.1",
		 "publicIP" : "112.324.125.155"
		}
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/rest/agent", produces="application/json; charset=UTF-8")
	public void agt_agt_i1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1.validate
			LayoutFilter.getInstance().validate("agt-agt-i1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. 시퀀스 획득
			requestMap.put("agtinf_seq", cmnService.nextval("AGTINF_SEQ"));
			requestMap.put("agentId", requestMap.get("id"));
			//3. DB저장
			agtService.AGT_AGT_I1000J(requestMap);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * Agent 수정
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253",
		 "version" : "1.0.1",
		 "os" : "windows 10",
		 "privateIP" : "192.168.0.1",
		 "publicIP" : "112.324.125.155"
		}
	 *
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value="/rest/agent/{id}", produces="application/json; charset=UTF-8")
	public void agt_agt_u1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1.validate
			LayoutFilter.getInstance().validate("agt-agt-u1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("agentId", id);
			
			//2. DB수정
			agtService.AGT_AGT_U1000J(requestMap);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * Agent 삭제
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253"
		}
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@DeleteMapping(value="/rest/agent/{id}", produces="application/json; charset=UTF-8")
	public void agt_agt_d1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1.validate
			LayoutFilter.getInstance().validate("agt-agt-d1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("agentId", id);
			
			//2. DB삭제
			agtService.AGT_AGT_D1000J(requestMap);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
			}
		} finally {
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
