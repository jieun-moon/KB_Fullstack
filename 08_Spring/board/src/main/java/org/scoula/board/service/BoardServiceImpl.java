package org.scoula.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.scoula.board.domain.BoardAttachmentVO;
import org.scoula.board.domain.BoardVO;
import org.scoula.board.dto.BoardDTO;
import org.scoula.board.mapper.BoardMapper;
import org.scoula.common.util.UploadFiles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.scoula.common.util.UploadFiles.upload;

@Log4j
@Service //Service 역할을 하는 Bean 등록
@RequiredArgsConstructor //final 필드로 생성자 추가
public class BoardServiceImpl implements BoardService {
//    업로드 시 해당 경로가 없으면 생성하도록 처리해뒀으므로 폴더가 없어도 상관없다
    private final static String BASE_DIR = "c:/upload/board";
    //final 멤버가 붙은 인자에 대해 생성자를 만들어주겠다
    //생성자가 하나 있다면 그 생성자로 주입 가능
    final private BoardMapper mapper;


    @Override
    public List<BoardDTO> getList() {
        log.info("getList...........");
//            mapper.getList(): List<BoardVO>를 원함
//        map: 모양을 변환(돌면서 요소 변환). BoardVO => BoardDTO로 변환
            return mapper.getList().stream() //BoardVO의 스트림
                    .map(BoardDTO::of) //전부 BoardDTO로 변환 -> BoardDTO의 stream
                    .toList(); //BoardDTO의 리스트로 변환(List<BoardDTO>변환)
    }
    //위의 코드에 pagination 기능을 구성해야함

    @Override
    public BoardDTO get(Long no) {
        log.info("get..........." + no);

        //null이 아닌 경우 .of
        //null이 아닌지 확신이 없을 경우 .ofNullable()
        //NoSuchElementException::new = RuntimeException
        //데이터 베이스에 없는 Long no 발생시켰을 때 예외 발생
        BoardDTO board = BoardDTO.of(mapper.get(no));
        //만약 board 객체가 null이면 "NoSuchElementException::new"예외 발생, null이 아니면 해당 객체 반환
        return Optional.ofNullable(board)
                .orElseThrow(NoSuchElementException::new);
    }

    //2개 이상의 insert 문이 실행될 수 있으므로 트랜잭션 처리 필요
    //RuntimeException 경우만 자동 rollback.
    @Transactional
    @Override
    public void create(BoardDTO board) {
        log.info("create......" + board);
//DTO를 VO로 변경해서 mapper의 메소드 호출
        BoardVO boardVO = board.toVo();
        mapper.create(boardVO); //boardVO: 후속 작업을 위해 필요함
//        변경된 vo의 no를 가져와서 다시 dto에 반영
        //no은 DTO객체가 아닌 VO에 들어감
//        board.setNo(vo.getNo());
//        Insert문이 작동될 때 첨부파일 작동갯수

        //파일 업로드 처리
        List<MultipartFile> files = board.getFiles();
        if(files != null && !files.isEmpty()) {//첨부 파일이 있는 경우
            upload(boardVO.getNo(), files);
//            boardVO.getNo(): FK
            //            예외발생: 자동 롤백
//            예외없으면 자동 커밋

        }

    }

    @Override
    public boolean update(BoardDTO board) {
        log.info("update......" + board);

//        transaction처리가 필요하므로 나중에 @Transational 불여야함
//        지금은 하나뿐이라 할 필요 없음
//        mapper의 update를 호출해서 수정된 행의 수가 1일 경우 true 반환
        return mapper.update(board.toVo()) == 1;
    }

    @Override
    public boolean delete(Long no) {
        log.info("delete......" + no);

//        삭제된 행의 수가 1인지 확인해서 boolean반환
        return mapper.delete(no) == 1;
    }

//    해당 게시물에 참조 파일들을 추가해주는 메소드
    private void upload(Long bno, List<MultipartFile> files) {
        for(MultipartFile part : files) {
//            첨부파일 목록에서 파일을 하나씩 꺼내서 비어있는지 확인
//            비어있으면, 다음 파일 확인
//            continue: 다음 아이템을 실행(다음 for문을 돎)
            if(part.isEmpty()) continue;
            try{
//                업로드 경로 생성 후 BoardAttachmentVO 객체 생성
//                UploadFiles.upload(BASE_DIR, part): 여기서 예외 던짐
                String uploadPath = UploadFiles.upload(BASE_DIR, part);
                BoardAttachmentVO attach = BoardAttachmentVO.of(part, bno, uploadPath);
//                BoardAttachmentVO 테이블에 참조파일 데이터 하나 추가
                mapper.createAttachment(attach); // 테이블에 추가
            } catch (IOException e) {
//                throw new RuntimeException(e): RuntimeException으로 바꿔서 리턴
                throw new RuntimeException(e); //@Transactional에서 감지, 자동 rollback
            }
        }
    }

    //첨부파일 한 개 얻기
    @Override
    public BoardAttachmentVO getAttachment(Long no) {
        return mapper.getAttachment(no);
    }

    //첨부파일 삭제
    @Override
    public boolean deleteAttachment(Long no) {
        return mapper.deleteAttachment(no) == 1;
    }
}
