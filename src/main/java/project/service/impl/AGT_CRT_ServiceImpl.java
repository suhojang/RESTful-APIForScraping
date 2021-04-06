package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_CRT_Service;

/**
 * 인증서정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("AGT_CRT_Service")
public class AGT_CRT_ServiceImpl extends ServiceIMPL implements AGT_CRT_Service {
	@Resource(name="AGT_AGT_Mapper")
	private AGT_AGT_Mapper agentDao;
	
	@Resource(name="AGT_CRT_Mapper")
	private AGT_CRT_Mapper dao;

	/**
	 * 인증서정보 조회
	 */
	@Override
	public List<Map<String,Object>> AGT_CRT_S1000J(Map<String,Object> param) throws Exception {
		int offset	= Integer.parseInt(String.valueOf(param.get("limit"))) * (Integer.parseInt(String.valueOf(param.get("pageNo")))-1);
		param.put("offset", offset);
		param.put("limit", Integer.parseInt(String.valueOf(param.get("limit"))));
		
		return dao.AGT_CRT_S1000J(param);
	}

	/**
	 * 인증서정보 등록
	 */
	@Override
	public void AGT_CRT_I1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> agent	= agentDao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");

			//1. 해당하는 인증서 아이디들은 전부 N으로 변경
			dao.AGT_CRT_U1001J(param);
			
			//2. N으로 설정 된 인증서 정보(certId, userId, agentId기준)가 있는지 확인
			Map<String,Object> info	= dao.AGT_CRT_V1001J(param);
			if(info != null) {
				//인증서 정보 변경
				dao.AGT_CRT_U1000J(param);
			} else {
				//인증서 신규 추가
				dao.AGT_CRT_I1000J(param);
			}
			
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "인증서정보 등록 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._921);
			throw ex;
		}
	}

	/**
	 * 인증서정보 수정
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_CRT_U1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			Map<String,Object> agent	= agentDao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			Map<String,Object> cert	= dao.AGT_CRT_V1000J(param);
			if (cert == null)
				throw new DefinedException("등록되지 않은 인증서정보 입니다.");
			
			dao.AGT_CRT_U1000J(param);
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "인증서정보 수정 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._922);
			throw ex;
		}
	}

	/**
	 * 인증서정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_CRT_D1000J(Map<String, Object> param) throws DefinedException {
		DefinedException ex	= null;
		try {
			/*Map<String,Object> cert	= dao.AGT_CRT_V1000J(param);
			if (cert == null)
				throw new DefinedException("등록되지 않은 인증서정보 입니다.");*/
			
			dao.AGT_CRT_D1000J(param);	//인증서정보 삭제
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "인증서정보 삭제 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._923);
			throw ex;
		}
	}
	
	/**
	 * Agent Master Key 조회
	 */
	@Override
	public String AGT_CRT_S1001J(Map<String,Object> param) throws DefinedException {
		DefinedException ex	= null;
		String key	= "";
		try {
			Map<String,Object> agent	= agentDao.AGT_AGT_V1000J(param);
			if (agent == null)
				throw new DefinedException("등록되지 않은 Agent 정보 입니다.");
			
			if (agent != null && agent.get("AGTINF_KEY") != null && !"".equals(agent.get("AGTINF_KEY"))) {
				key	= String.valueOf(agent.get("AGTINF_KEY"));
			}
		} catch (Exception e) {
			logger.error(e);
			
			ex	= new DefinedException(e instanceof DefinedException ? e.getMessage() : "Agent Master Key 생성 중 오류가 발생 하였습니다.");
			ex.setStatusCode(ErrorCode._924);
			throw ex;
		}
		return key;
	}

	/**
	 * Agent Master Key 정보 추가
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_CRT_U1001J(Map<String, Object> param) throws DefinedException {
		try {
			agentDao.AGT_AGT_U1001J(param);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
