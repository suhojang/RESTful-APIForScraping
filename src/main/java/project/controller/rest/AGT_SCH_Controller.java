package project.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.quartz.CronExpression;
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
import com.kwic.json.org.JSONObject;
import com.kwic.util.DateUtil;
import com.kwic.util.PluginUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.AGT_SCH_Service;
import project.service.AGT_TSK_Service;
import project.service.CMN_CMN_Service;

/**
 * 스케쥴 정보 Controller
 * 
 * @date 2020.08.03
 * @author 장수호
 *
 */
@RestController
public class AGT_SCH_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "AGT_SCH_Service")
	private AGT_SCH_Service schService;
	
	@Resource(name = "AGT_TSK_Service")
	private AGT_TSK_Service tskService;
	
	/**
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/schedule", produces="application/json; charset=UTF-8")
	public void agt_sch_s1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-sch-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//스케쥴정보 조회
			List<Map<String,Object>> list	= schService.AGT_SCH_S1000J(requestMap);
			
			List<Map<String,Object>> schedules	= new ArrayList<Map<String,Object>>();
			Map<String,Object> schedule			= null;
			
			for (int i = 0; i < list.size(); i++) {
				schedule	= new HashMap<String,Object>();
				
				/********************************** 스케쥴 정보 ***********************************/
				schedule.put("id", 		String.valueOf(list.get(i).get("SCHINF_ID")));
				schedule.put("userId", 	String.valueOf(list.get(i).get("SCHINF_USRID")));
				schedule.put("certId", 	String.valueOf(list.get(i).get("SCHINF_CRTID")));
				schedule.put("fcode", 	String.valueOf(list.get(i).get("SCHINF_FCD")));
				schedule.put("module", 	String.valueOf(list.get(i).get("SCHINF_MDL")));
				schedule.put("kind", 	String.valueOf(list.get(i).get("SCHINF_KN")));
				schedule.put("cron", 	String.valueOf(list.get(i).get("SCHINF_CRON")));
				schedule.put("data", 	new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				/**********************************& 스케쥴 정보 &***********************************/
				
				//스케쥴 정보 담기
				schedules.add(schedule);
			}
			//스케쥴정보 전체 담기
			responseMap.put("schedules", schedules);
			
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
	 * 스케쥴정보 조회 - 스케쥴ID로 조회
	 * 
	 * @param requestContent
	 * @param request
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/rest/schedule/{id}", produces="application/json; charset=UTF-8")
	public void agt_sch_s1001j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-sch-s1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("scheduleId", id);
			
			//페이지 설정
			if (requestMap.get("pageNo")==null || "".equals(String.valueOf(requestMap.get("pageNo"))))
				requestMap.put("pageNo", properties.getInt("pageNo"));
			
			if (requestMap.get("limit")==null || "".equals(String.valueOf(requestMap.get("limit"))))
				requestMap.put("limit", properties.getInt("limit"));
			
			//스케쥴정보 조회
			List<Map<String,Object>> list	= schService.AGT_SCH_S1000J(requestMap);
			
			List<Map<String,Object>> schedules	= new ArrayList<Map<String,Object>>();
			Map<String,Object> schedule			= null;
			
			for (int i = 0; i < list.size(); i++) {
				schedule	= new HashMap<String,Object>();
				
				/********************************** 스케쥴 정보 ***********************************/
				schedule.put("id", 		String.valueOf(list.get(i).get("SCHINF_ID")));
				schedule.put("userId", 	String.valueOf(list.get(i).get("SCHINF_USRID")));
				schedule.put("certId", 	String.valueOf(list.get(i).get("SCHINF_CRTID")));
				schedule.put("fcode", 	String.valueOf(list.get(i).get("SCHINF_FCD")));
				schedule.put("module", 	String.valueOf(list.get(i).get("SCHINF_MDL")));
				schedule.put("kind", 	String.valueOf(list.get(i).get("SCHINF_KN")));
				schedule.put("cron", 	String.valueOf(list.get(i).get("SCHINF_CRON")));
				schedule.put("data", 	new JSONObject(String.valueOf(list.get(i).get("SCHINF_DATA"))).toMap());
				/**********************************& 스케쥴 정보 &***********************************/
				
				//스케쥴 정보 담기
				schedules.add(schedule);
			}
			//스케쥴정보 전체 담기
			responseMap.put("schedules", schedules);
			
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
	 * 스케쥴정보 등록
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/rest/schedule", produces="application/json; charset=UTF-8")
	public void agt_crt_i1000j(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		CronExpression cron	= null;
		try {
			//인증
			validateRemoteIP(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1. validate
			LayoutFilter.getInstance().validate("agt-sch-i1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. Quartz Cron 유효성 검증
			try {
				cron		= new CronExpression(String.valueOf(requestMap.get("cron")));
			} catch (java.text.ParseException e) {
				throw new DefinedException("요청하신 스케쥴의 Quartz Cron 형식이 올바르지 않습니다. 참조 사이트 [https://www.freeformatter.com/cron-expression-generator-quartz.html]");
			}
			
			try {
				java.util.Date nexTime	= cron.getNextValidTimeAfter(new java.util.Date());
				long diff		= Math.abs(nexTime.getTime() - new java.util.Date().getTime()) / 1000;
				if (diff < 0)
					throw new Exception();
			} catch (Exception e) {
				throw new DefinedException("요청하신 스케쥴의 Quartz Cron 형식은 이미 지난 시간 입니다. 참조 사이트 [https://www.freeformatter.com/cron-expression-generator-quartz.html]");
			}
			
			//3. 시퀀스 획득
			requestMap.put("schinf_seq", cmnService.nextval("SCHINF_SEQ"));
			requestMap.put("scheduleId", PluginUtil.getScheduleKey());
			
			//4. DB저장
			schService.AGT_SCH_I1000J(requestMap);
			
			//5. 1회성 스케쥴 TASK정보 등록
			if ("1".equals(requestMap.get("kind"))) {
				Map<String,Object> params	= new HashMap<String, Object>();
				params.put("tskinf_seq", 	cmnService.nextval("TSKINF_SEQ"));
				params.put("taskId", 		PluginUtil.getTaskKey());
				params.put("scheduleId", 	requestMap.get("scheduleId"));
				params.put("excdt", 		DateUtil.dateToString(cron.getNextValidTimeAfter(new java.util.Date()), "yyyyMMddHHmmss"));
				params.put("status", 		"1");	//대기-배치job등록
				
				tskService.AGT_TSK_I1000J(params);	//Task정보 등록
			}
			
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
	 * 스케쥴정보 수정
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value="/rest/schedule/{id}", produces="application/json; charset=UTF-8")
	public void agt_sch_u1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		
		CronExpression cron	= null;
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1. validate
			LayoutFilter.getInstance().validate("agt-sch-u1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2. Quartz Cron 유효성 검증
			try {
				cron		= new CronExpression(String.valueOf(requestMap.get("cron")));
			} catch (java.text.ParseException e) {
				throw new DefinedException("요청하신 스케쥴의 Quartz Cron 형식이 올바르지 않습니다. 참조 사이트 [https://www.freeformatter.com/cron-expression-generator-quartz.html]");
			}
			
			try {
				java.util.Date nexTime	= cron.getNextValidTimeAfter(new java.util.Date());
				long diff		= Math.abs(nexTime.getTime() - new java.util.Date().getTime()) / 1000;
				if (diff < 0)
					throw new Exception();
			} catch (Exception e) {
				throw new DefinedException("요청하신 스케쥴의 Quartz Cron 형식은 이미 지난 시간 입니다. 참조 사이트 [https://www.freeformatter.com/cron-expression-generator-quartz.html]");
			}
			
			requestMap.put("scheduleId", id);
			
			//3. DB수정
			schService.AGT_SCH_U1000J(requestMap);
			
			//4. Task정보 삭제(아직 수행되지 않은 Task정보)
			requestMap.put("status", "1");
			tskService.AGT_TSK_D1000J(requestMap);
			
			//5. 1회성 스케쥴 TASK정보 등록
			if ("1".equals(requestMap.get("kind"))) {
				Map<String,Object> params	= new HashMap<String, Object>();
				params.put("tskinf_seq", 	cmnService.nextval("TSKINF_SEQ"));
				params.put("taskId", 		PluginUtil.getTaskKey());
				params.put("scheduleId", 	requestMap.get("scheduleId"));
				params.put("excdt", 		DateUtil.dateToString(cron.getNextValidTimeAfter(new java.util.Date()), "yyyyMMddHHmmss"));
				params.put("status", 		"1");	//대기-배치job등록
				
				tskService.AGT_TSK_I1000J(params);	//Task정보 등록
			}
			
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
	 * 스케쥴정보 삭제
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@DeleteMapping(value="/rest/schedule/{id}", produces="application/json; charset=UTF-8")
	public void agt_sch_d1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-sch-d1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("scheduleId", id);
			
			//2. DB삭제
			schService.AGT_SCH_D1000J(requestMap);
			
			//3. Task정보 삭제
			tskService.AGT_TSK_D1000J(requestMap);
			
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
}
