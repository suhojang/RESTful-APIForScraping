package project.service;

import java.util.List;
import java.util.Map;

import com.kwic.exception.DefinedException;

/**
 * Agent정보 조회/등록/수정/삭제 처리
 * @author 장수호
 *
 */
public interface AGT_SYN_Service {

	/**
	 * Agent정보 동기화 - 삭제목록 반환
	 * @param requestMap
	 * @throws DefinedException
	 */
	List<Map<String,Object>> AGT_SYN_U1000J(Map<String, Object> param) throws DefinedException;
}
