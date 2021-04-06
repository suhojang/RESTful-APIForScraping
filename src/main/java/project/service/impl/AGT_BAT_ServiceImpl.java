package project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kwic.exception.DefinedException;
import com.kwic.service.ServiceIMPL;

import project.service.AGT_BAT_Service;

/**
 * Batch 정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
@Service("AGT_BAT_Service")
public class AGT_BAT_ServiceImpl extends ServiceIMPL implements AGT_BAT_Service {
	
	@Resource(name="AGT_BAT_Mapper")
	private AGT_BAT_Mapper dao;

	/**
	 * Batch 정보 조회
	 */
	@Override
	public List<Map<String,Object>> AGT_BAT_S1000J(Map<String,Object> param) throws Exception {
		return dao.AGT_BAT_S1000J(param);
	}
	

	/**
	 * Batch 정보 단일 조회
	 */
	@Override
	public Map<String, Object> AGT_BAT_V1000J(Map<String, Object> param) throws Exception {
		return dao.AGT_BAT_V1000J(param);
	}

	/**
	 * Batch 정보 등록
	 */
	@Override
	public void AGT_BAT_I1000J(Map<String, Object> param) throws Exception {
		try {
			dao.AGT_BAT_I1000J(param);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Batch 정보 수정
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_BAT_U1000J(Map<String, Object> param) throws Exception {
		try {
			dao.AGT_BAT_U1000J(param);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Batch 정보 구동시간 등록
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_BAT_U1001J(Map<String, Object> param) throws Exception {
		try {
			dao.AGT_BAT_U1001J(param);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Batch 정보 삭제
	 * @param param
	 * @throws DefinedException
	 */
	@Override
	public void AGT_BAT_D1000J(Map<String, Object> param) throws Exception {
		try {
			dao.AGT_BAT_D1000J(param);
		} catch (Exception e) {
			throw e;
		}
	}
}
