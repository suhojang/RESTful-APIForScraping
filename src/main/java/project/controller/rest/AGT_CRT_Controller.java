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
import com.kwic.security.aes.AES;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.AGT_CRT_Service;
import project.service.CMN_CMN_Service;

/**
 * 인증서 정보 Controller
 * 
 * @date 2020.07.24
 * @author 장수호
 *
 */
@RestController
public class AGT_CRT_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "AGT_CRT_Service")
	private AGT_CRT_Service crtService;
	
	/**
	 * 인증서 전체 조회
	 * 
	 * - 요청
	 * {
		 "agentId" : "agentId1",
		 "userId" : "user1",
		 "pageNo" : "1",
		 "limit" : "10"
		 }
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		 "certs":[{
		  "certId" : "certid1",
		  "userId" : "userId1",
		  "agentId" : "agent1",
		  "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		  "expire" : "2021-07-21 15:40:22"
		 },{
		  "certId" : "certid2",
		  "userId" : "userId1",
		  "agentId" : "agent1",
		  "certnm" : "cn=홍길동(HONG GIL DONG)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		  "expire" : "2021-07-21 15:40:22"
		 }]
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/cert", produces="application/json; charset=UTF-8")
	public void agt_crt_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validate(request);
			try {	
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			
			//1.validate
			LayoutFilter.getInstance().validate("agt-crt-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//인증서정보 조회
			List<Map<String,Object>> list	= crtService.AGT_CRT_S1000J(requestMap);
			
			List<Map<String,Object>> certs		= new ArrayList<Map<String,Object>>();
			Map<String,Object> cert			= null;
			
			for (int i = 0; i < list.size(); i++) {
				cert	= new HashMap<String,Object>();
				
				/********************************** 인증서 정보 ***********************************/
				cert.put("certId", 	String.valueOf(list.get(i).get("CRTINF_CRTID")));
				cert.put("userId", 	String.valueOf(list.get(i).get("CRTINF_USRID")));
				cert.put("agentId", String.valueOf(list.get(i).get("AGTINF_ID")));
				cert.put("certnm", 	String.valueOf(list.get(i).get("CRTINF_CRTNM")));
				cert.put("expire", 	String.valueOf(list.get(i).get("CRTINF_EXPIRE")));
				/**********************************& 인증서 정보 &***********************************/
				
				//인증서 정보 담기
				certs.add(cert);
			}
			//인증서정보 전체 담기
			responseMap.put("certs", certs);
			
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
			/*responseMap.put(AGT_STATUS, "ok");
			responseMap.put(AGT_MESSAGE, "success");
			responseMap.put(AGT_ERROR, "");*/
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * 인증서정보 조회 - 인증서ID로 조회
	 * 
	 * - 요청
	 * {
		 "agentId" : "agentId1",
		 "userId" : "user1",
		 "pageNo" : "1",
		 "limit" : "10"
		 }
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : "",
		 "certs":[{
		  "certId" : "certid1",
		  "userId" : "userId1",
		  "agentId" : "agent1",
		  "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		  "expire" : "2021-07-21 15:40:22"
		 },{
		  "certId" : "certid2",
		  "userId" : "userId1",
		  "agentId" : "agent1",
		  "certnm" : "cn=홍길동(HONG GIL DONG)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		  "expire" : "2021-07-21 15:40:22"
		 }]
		}
	 * 
	 * 
	 * @param requestContent
	 * @param request
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/cert/{id}", produces="application/json; charset=UTF-8")
	public void agt_crt_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validate(request);
			try {	
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			
			//1.validate
			LayoutFilter.getInstance().validate("agt-crt-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("certId", id);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//인증서정보 조회
			List<Map<String,Object>> list	= crtService.AGT_CRT_S1000J(requestMap);
			
			List<Map<String,Object>> certs		= new ArrayList<Map<String,Object>>();
			Map<String,Object> cert			= null;
			
			for (int i = 0; i < list.size(); i++) {
				cert	= new HashMap<String,Object>();
				
				/********************************** 인증서 정보 ***********************************/
				cert.put("certId", 	String.valueOf(list.get(i).get("CRTINF_CRTID")));
				cert.put("userId", 	String.valueOf(list.get(i).get("CRTINF_USRID")));
				cert.put("agentId", String.valueOf(list.get(i).get("AGTINF_ID")));
				cert.put("certnm", 	String.valueOf(list.get(i).get("CRTINF_CRTNM")));
				cert.put("expire", 	String.valueOf(list.get(i).get("CRTINF_EXPIRE")));
				/**********************************& 인증서 정보 &***********************************/
				
				//인증서 정보 담기
				certs.add(cert);
			}
			//인증서정보 전체 담기
			responseMap.put("certs", certs);
			
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
			/*responseMap.put(AGT_STATUS, "ok");
			responseMap.put(AGT_MESSAGE, "success");
			responseMap.put(AGT_ERROR, "");*/
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
	
	/**
	 * 인증서정보 등록
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253",
		 "userId" : "userId1",
		 "certId" : "certid1",
		 "agentId" : "agentId",
		 "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		 "expire" : "2021-07-21 15:40:22"
		}
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : ""
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/rest/cert", produces="application/json; charset=UTF-8")
	public void agt_crt_i1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-crt-i1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. 시퀀스 획득
			requestMap.put("crtinf_seq", cmnService.nextval("CRTINF_SEQ"));
			
			//3. DB저장
			crtService.AGT_CRT_I1000J(requestMap);
			
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
	 * 인증서정보 수정
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253",
		 "userId" : "userId1",
		 "agentId" : "agentId",
		 "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		 "expire" : "2021-07-21 15:40:22"
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
	@PutMapping(value="/rest/cert/{id}", produces="application/json; charset=UTF-8")
	public void agt_crt_u1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-crt-u1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("certId", id);
			
			//2. DB수정
			crtService.AGT_CRT_U1000J(requestMap);
			
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
	 * 인증서정보 삭제
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
	@DeleteMapping(value="/rest/cert/{id}", produces="application/json; charset=UTF-8")
	public void agt_crt_d1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-crt-d1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("certId", id);
			
			//2. DB삭제
			crtService.AGT_CRT_D1000J(requestMap);
			
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
	 * Agent 인증서 암호화 키 Server 암호화 후 반환
	 * 
	 * 1) Agent Master Key가 존재하지 않을 시 Master Key(32bytes) 생성
	 * 2) Agent 인증서 암호화 키를 Master키로  AES256 BASE64 암호화.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/cert/enckey", produces="application/json; charset=UTF-8")
	public void agt_key_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-key-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. Master Key 조회
			String key	= crtService.AGT_CRT_S1001J(requestMap);
			
			//3. Master Key 없을 시 생성 및 저장
			if (key == null || "".equals(key)) {
				key			= StringUtil.getMasterKey(AES.TYPE_256);
				
				Map<String,Object> param	= new HashMap<String,Object>();
				param.put("agentId", requestMap.get("agentId"));
				param.put("key", key);	//AES256 키생성
				
				crtService.AGT_CRT_U1001J(param);
			}
			
			//4. agentEncKey AES256 BASE64 암호화
			String encKey	= AES.encode(
					String.valueOf(requestMap.get("agentEncKey"))
					,key
					,AES.TYPE_256
					,AES.MODE_CBC
					);
			
			//5. server Encrypt Key return
			responseMap.put("agentId", 		requestMap.get("agentId"));
			responseMap.put("serverEncKey", encKey);
			
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
	 * Agent 인증서 암호화 키를 Master키로  AES256 BASE64 복호화.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/cert/deckey", produces="application/json; charset=UTF-8")
	public void agt_key_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-key-s1001j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. Master Key 조회
			String key	= crtService.AGT_CRT_S1001J(requestMap);
			
			//3. Master Key 없을 시 생성 및 저장
			if (key == null || "".equals(key)) {
				throw new DefinedException("Agent의 Master Key가 등록 되어 있지 않습니다.");
			}
			
			//4. agentEncKey AES256 BASE64 암호화
			String decKey	= AES.decode(
					String.valueOf(requestMap.get("serverEncKey"))
					,key
					,AES.TYPE_256
					,AES.MODE_CBC
					);
			
			//5. server Encrypt Key return
			responseMap.put("agentId", 		requestMap.get("agentId"));
			responseMap.put("agentEncKey", 	decKey);
			
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
