package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.json.org.JSONObject;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_TSK_Service;

/**
 * Task정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("AGT_TSK_Service")
public class AGT_TSK_ServiceImpl extends ServiceIMPL implements AGT_TSK_Service {
	@Resource(name="AGT_AGT_Mapper")
	private AGT_AGT_Mapper agtdao;
	
	@Resource(name="AGT_TSK_Mapper")
	private AGT_TSK_Mapper dao;
	

	/**
	 * Task정보 조회
	 */
	@Override
	public List<Map<String,Object>> AGT_TSK_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.AGT_TSK_S1000J(param);
	}
	

	/**
	 * Task정보 단일 조회
	 */
	@Override
	public Map<String, Object> AGT_TSK_V1000J(Map<String, Object> param) throws Exception {
		return dao.AGT_TSK_V1000J(param);
	}

	/**
	 * Task 상태값 변경
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_TSK_U1001J(Map<String, Object> param) throws DefinedException {
		try {
			dao.AGT_TSK_U1001J(param);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Task 결과 데이터 등록
	 * @param param
	 * @throws DefinedException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void AGT_TSK_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> agent	= agtdao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			Map<String,Object> task	= dao.AGT_TSK_V1000J(param);
			if (task == null)
				throw new DefinedException("존재하지 않은 Task정보 입니다.");
			
			JSONObject obj	= new JSONObject((Map<String,Object>)param.get("result"));
			param.put("result", obj.toString());
			
			dao.AGT_TSK_U1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Task결과 데이터 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._951);
			throw ex;
		}
	}

	/**
	 * Task 수행 데이터 등록
	 */
	@Override
	public void AGT_TSK_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			dao.AGT_TSK_I1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Task수행 데이터 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._951);
			throw ex;
		}
	}

	/**
	 * Task 정보 삭제
	 */
	@Override
	public void AGT_TSK_D1000J(Map<String, Object> param) throws DefinedException {
		try {
			dao.AGT_TSK_D1000J(param);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
