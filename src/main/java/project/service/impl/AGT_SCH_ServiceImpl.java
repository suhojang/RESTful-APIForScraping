package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.json.org.JSONObject;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_SCH_Service;

/**
 * 스케쥴정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("AGT_SCH_Service")
public class AGT_SCH_ServiceImpl extends ServiceIMPL implements AGT_SCH_Service {
	@Resource(name="AGT_CRT_Mapper")
	private AGT_CRT_Mapper crtDao;
	
	@Resource(name="AGT_SCH_Mapper")
	private AGT_SCH_Mapper dao;

	/**
	 * 스케쥴정보 조회
	 */
	@Override
	public List<Map<String,Object>> AGT_SCH_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.AGT_SCH_S1000J(param);
	}
	

	/**
	 * 스케쥴정보 단일 조회
	 */
	@Override
	public Map<String, Object> AGT_SCH_V1000J(Map<String, Object> param) throws Exception {
		return dao.AGT_SCH_V1000J(param);
	}

	/**
	 * 스케쥴정보 등록
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void AGT_SCH_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			param.put("certId", param.get("certId"));
			param.put("userId", param.get("userId"));
			Map<String,Object> agent	= crtDao.AGT_CRT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			Map<String,Object> schedule	= dao.AGT_SCH_V1000J(param);
			if (schedule != null)
				throw new DefinedException("이미 등록 된 스케쥴정보 입니다.");
			
			param.put("agentId", String.valueOf(agent.get("AGTINF_ID")));
			
			JSONObject obj	= new JSONObject((Map<String,Object>)param.get("data"));
			param.put("data", obj.toString());
			
			dao.AGT_SCH_I1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "스케쥴정보 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._941);
			throw ex;
		}
	}

	/**
	 * 스케쥴정보 수정
	 * @param param
	 * @throws DefinedException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void AGT_SCH_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			param.put("certId", param.get("certId"));
			param.put("userId", param.get("userId"));
			Map<String,Object> agent	= crtDao.AGT_CRT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			Map<String,Object> schedule	= dao.AGT_SCH_V1000J(param);
			if (schedule == null)
				throw new DefinedException("등록되지 않은 스케쥴정보 입니다.");
			
			JSONObject obj	= new JSONObject((Map<String,Object>)param.get("data"));
			param.put("data", obj.toString());
			
			dao.AGT_SCH_U1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "스케쥴정보 수정 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._942);
			throw ex;
		}
	}

	/**
	 * 스케쥴정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_SCH_D1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> schedule	= dao.AGT_SCH_V1000J(param);
			if (schedule == null)
				throw new DefinedException("등록되지 않은 스케쥴정보 입니다.");
			
			dao.AGT_SCH_D1000J(param);	//스케쥴정보 삭제
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "스케쥴정보 삭제 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._943);
			throw ex;
		}
	}

	/**
	 * Task할당 스케쥴 정보 조회
	 */
	@Override
	public List<Map<String, Object>> AGT_SCH_S3000J(Map<String, Object> param) throws Exception {
		return dao.AGT_SCH_S3000J(param);
	}
}
