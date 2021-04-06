package project.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.json.org.JSONObject;
import com.kwic.telegram.rest.RestClient;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.AGT_AGT_Service;
import project.service.AGT_SCH_Service;
import project.service.AGT_TSK_Service;
import project.service.CMN_CMN_Service;

/**
 * TASK 정보 Controller
 * 
 * @date 2020.08.04
 * @author 장수호
 *
 */
@RestController
public class AGT_TSK_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "AGT_TSK_Service")
	private AGT_TSK_Service service;
	
	@Resource(name = "AGT_SCH_Service")
	private AGT_SCH_Service schService;
	
	@Resource(name = "AGT_AGT_Service")
	private AGT_AGT_Service agtService;
	
	/**
	 * task 정보 조회 - AGENT
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/tasks", produces="application/json; charset=UTF-8")
	public void agt_tsk_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			
			//agent에서 접근 시 agentId정보(필수) 누락 여부 확인 및 유효성 검증
			String token		= request.getHeader("Authorization");
			if(token != null) {
				token	= token.replace("Token ", "");
				if (token.equals(properties.getString("agent.token"))) {
					if(requestMap.get("agentId")==null || "".equals(requestMap.get("agentId"))) {
						throw new DefinedException("[agentId] 항목은 필수입력 사항입니다.");
					} else {
						Map<String,Object> agent	= agtService.AGT_AGT_V1000J(requestMap);
						if (agent == null)
							throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
					}
				}
			}
		
			//1.validate
			LayoutFilter.getInstance().validate("agt-tsk-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			requestMap.put("status", "'1','2'");	//대기-배치job등록 상태, 할당-agent수신 상태 인 것만 조회
			//TASK정보 조회
			List<Map<String,Object>> list	= service.AGT_TSK_S1000J(requestMap);
			
			List<Map<String,Object>> tasks	= new ArrayList<Map<String,Object>>();
			Map<String,Object> task			= null;
			
			String receiveTask	= "";
			for (int i = 0; i < list.size(); i++) {
				task	= new HashMap<String,Object>();
				
				/********************************** TASK 정보 ***********************************/
				task.put("id", 				String.valueOf(list.get(i).get("TSKINF_ID")));
				task.put("excuteDate", 		String.valueOf(list.get(i).get("TSKINF_EXCDT")));
				task.put("statusCd", 		String.valueOf(list.get(i).get("TSKINF_STS")));
				task.put("data", 			new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				/**********************************& TASK 정보 &***********************************/
				
				receiveTask += !"".equals(receiveTask) ? "," : "";
				receiveTask += "'" + task.get("id") + "'";
				//TASK 정보 담기
				tasks.add(task);
			}
			//TASK정보 전체 담기
			responseMap.put("tasks", tasks);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
			//task 상태값 변경 : 2.할당-agent수신
			if (!"".equals(receiveTask)) {
				Map<String,Object> params	= new HashMap<String,Object>();
				params.put("status", "2");	//할당-agent수신
				params.put("receiveTask", receiveTask);
				
				service.AGT_TSK_U1001J(params);
			}
			
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
	 * task 정보 조회 - AGENT(task ID로 조회)
	 * 
	 * @param requestContent
	 * @param request
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/tasks/{id}", produces="application/json; charset=UTF-8")
	public void agt_tsk_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-tsk-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("taskId", id);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//TASK정보 조회
			List<Map<String,Object>> list	= service.AGT_TSK_S1000J(requestMap);
			
			List<Map<String,Object>> tasks	= new ArrayList<Map<String,Object>>();
			Map<String,Object> task			= null;
			
			String receiveTask	= "";
			for (int i = 0; i < list.size(); i++) {
				task	= new HashMap<String,Object>();
				
				/********************************** TASK 정보 ***********************************/
				task.put("id", 				String.valueOf(list.get(i).get("TSKINF_ID")));
				task.put("excuteDate", 		String.valueOf(list.get(i).get("TSKINF_EXCDT")));
				task.put("statusCd", 		String.valueOf(list.get(i).get("TSKINF_STS")));
				task.put("data", 			new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				/**********************************& TASK 정보 &***********************************/
				
				receiveTask += !"".equals(receiveTask) ? "," : "";
				receiveTask += "'" + task.get("id") + "'";
				
				//TASK 정보 담기
				tasks.add(task);
			}
			//TASK정보 전체 담기
			responseMap.put("tasks", tasks);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
			//task 상태값 변경 : 2.할당-agent수신
			if (!"".equals(receiveTask)) {
				Map<String,Object> params	= new HashMap<String,Object>();
				params.put("status", "2");	//할당-agent수신
				params.put("receiveTask", receiveTask);
				
				service.AGT_TSK_U1001J(params);
			}
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
	 * task 결과 데이터 조회 - 장기요양
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/tasks/result", produces="application/json; charset=UTF-8")
	public void agt_rst_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-tsk-s1001j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//TASK정보 결과 조회
			List<Map<String,Object>> list	= service.AGT_TSK_S1000J(requestMap);
			
			List<Map<String,Object>> tasks	= new ArrayList<Map<String,Object>>();
			Map<String,Object> task			= null;
			
			for (int i = 0; i < list.size(); i++) {
				task	= new HashMap<String,Object>();
				
				/********************************** TASK 정보 ***********************************/
				task.put("id", 				String.valueOf(list.get(i).get("TSKINF_ID")));
				task.put("scheduleId", 		String.valueOf(list.get(i).get("SCHINF_ID")));
				task.put("excuteDate", 		String.valueOf(list.get(i).get("TSKINF_EXCDT")));
				task.put("statusCd", 		String.valueOf(list.get(i).get("TSKINF_STS")));
				task.put("data", 			new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				task.put("agentId", 		String.valueOf(list.get(i).get("AGTINF_ID")));
				task.put("result", 			list.get(i).get("TSKINF_RST")!=null ? new JSONObject(String.valueOf(list.get(i).get("TSKINF_RST"))).toMap() : list.get(i).get("TSKINF_RST"));
				task.put("resultRegDate", 	String.valueOf(list.get(i).get("TSKINF_RSTDT")));
				/**********************************& TASK 정보 &***********************************/
				
				//TASK 결과 정보 담기
				tasks.add(task);
			}
			//TASK정보 결과 전체 담기
			responseMap.put("tasks", tasks);
			
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
	 * task 결과 데이터 조회 - 장기요양(task ID 포함)
	 * @param requestContent
	 * @param request
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/tasks/result/{id}", produces="application/json; charset=UTF-8")
	public void agt_rst_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-tsk-s1001j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("taskId", id);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//TASK정보 조회
			List<Map<String,Object>> list	= service.AGT_TSK_S1000J(requestMap);
			
			List<Map<String,Object>> tasks	= new ArrayList<Map<String,Object>>();
			Map<String,Object> task			= null;
			
			for (int i = 0; i < list.size(); i++) {
				task	= new HashMap<String,Object>();
				
				/********************************** TASK 정보 ***********************************/
				task.put("id", 				String.valueOf(list.get(i).get("TSKINF_ID")));
				task.put("scheduleId", 		String.valueOf(list.get(i).get("SCHINF_ID")));
				task.put("excuteDate", 		String.valueOf(list.get(i).get("TSKINF_EXCDT")));
				task.put("statusCd", 		String.valueOf(list.get(i).get("TSKINF_STS")));
				task.put("data", 			new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				task.put("agentId", 		String.valueOf(list.get(i).get("AGTINF_ID")));
				task.put("result", 			list.get(i).get("TSKINF_RST")!=null ? new JSONObject(String.valueOf(list.get(i).get("TSKINF_RST"))).toMap() : list.get(i).get("TSKINF_RST"));
				task.put("resultRegDate", 	String.valueOf(list.get(i).get("TSKINF_RSTDT")));
				/**********************************& TASK 정보 &***********************************/
				
				//TASK 정보 담기
				tasks.add(task);
			}
			//TASK정보 전체 담기
			responseMap.put("tasks", tasks);
			
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
	 * TASK 결과 데이터 등록
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value="/rest/tasks/result/{id}", produces="application/json; charset=UTF-8")
	public void agt_sch_u1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		Map<String,Object> info			= null; 
		Map<String,Object> task			= new HashMap<String,Object>();
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1.validate
			LayoutFilter.getInstance().validate("agt-tsk-u1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("taskId", id);
			requestMap.put("status", "3");	//완료-결과값수신
			
			//2. DB수정
			service.AGT_TSK_U1000J(requestMap);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
			
			//3. Callback 보낼 Task정보 담기
			info	= service.AGT_TSK_V1000J(requestMap);
			
			/********************************** TASK 정보 ***********************************/
			task.put("id", 				String.valueOf(info.get("TSKINF_ID")));
			task.put("scheduleId", 		String.valueOf(info.get("SCHINF_ID")));
			task.put("excuteDate", 		String.valueOf(info.get("TSKINF_EXCDT")));
			task.put("statusCd", 		String.valueOf(info.get("TSKINF_STS")));
			task.put("data", 			new JSONObject(String.valueOf(info.get("SCHINF_DATA"))).toMap());
			task.put("agentId", 		String.valueOf(info.get("AGTINF_ID")));
			task.put("result", 			info.get("TSKINF_RST")!=null ? new JSONObject(String.valueOf(info.get("TSKINF_RST"))).toMap() : info.get("TSKINF_RST"));
			task.put("resultRegDate", 	String.valueOf(info.get("TSKINF_RSTDT")));
			/**********************************& TASK 정보 &***********************************/
			
			//4. 이미 삭제 된 스케쥴인지 확인하여 callback여부 체크
			Map<String,Object> params	= new HashMap<String,Object>();
			params.put("scheduleId", task.get("scheduleId"));
			
			info	= schService.AGT_SCH_V1000J(params);
			
			if (info !=null) {
				//5. 장기요양 call back url로 데이터 송신
				String callbackData				= RestClient.sendJSON(properties.getString("server.callback"), new ObjectMapper().writeValueAsString(task));
				
				/** 응답 데이터 분석 후 처리 할지는 추후 결정 **/
				/*
				Map<String,Object> callbackMap	= (Map<String, Object>) new ObjectMapper().readValue(callbackData, HashMap.class);
				//6. resData분석 후 재요청 확인
				String yn	= String.valueOf(callbackMap.get(""));
				*/
				
				//7. task상태값 변경
				params	= new HashMap<String,Object>();
				params.put("status", "9");	//CALLBACK-장기요양시스템 Callback 완료
				params.put("receiveTask", "'"+id+"'");
				
				service.AGT_TSK_U1001J(params);
			}
			
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
}
