package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * Task정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface AGT_TSK_Service {

	/**
	 * Task정보 조회
	 * @param param
	 * @throws Exception
	 */
	List<Map<String,Object>> AGT_TSK_S1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * Task정보 단일 조회
	 * @param param
	 * @throws Exception
	 */
	Map<String,Object> AGT_TSK_V1000J(Map<String,Object> param) throws Exception;
	
	/**
	 * Task 상태값 변경
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_TSK_U1001J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * Task 결과 데이터 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_TSK_U1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * Task 수행 데이터 등록
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_TSK_I1000J(Map<String, Object> param) throws DefinedException;
	
	/**
	 * Task 정보 삭제
	 * @param requestMap
	 * @throws DefinedException
	 */
	void AGT_TSK_D1000J(Map<String, Object> param) throws DefinedException;
}
