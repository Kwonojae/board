package model;

import java.awt.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class BoardDAO {



	//데이터 베이스에 연결 메소드
	private static BoardDAO dao = new BoardDAO();	

	public BoardDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("OracleDriver load Success");
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
	}
	//OBJ == 그릇  
	public static BoardDAO getInstance() {
		return dao;
	}

	/*한개의 게시글에 대한 내용을 가져오는 메소드
	public BoardBean getBoardContent(int num) {
		// return 값은 게시글에 대한 정보를 돌려주는 Dto
		//매개변수로 받게되는 값은 해당 게시글에 대한 IndexNumber
		BoardBean dto = new BoardBean();

		String sql ="SELECT * FROM BOARD WHERE NUM = ?";
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection();//나는 디비에 접속을 할것입니다 성현좌 왈

			psmt = conn.prepareStatement(sql);

			psmt.setInt(1, num);

			//쿼리 실행후 결과를 리턴
			rs = psmt.executeQuery();
			// 단일 행 함수 이기때문에 while문을 돌릴 필요가없다
			if(rs.next()) {	//데이터가 있다면
				//정보를 받는 애를 해주고
				dto.setContent(rs.getString("CONTENT"));
			}
			conn.close();
			psmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}
	 */

	//전체 게시글의 갯수를 리턴하는 메소드 
	public int getAllCount() {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int count = 0;

		try {
			con = this.getConnection();//나는 디비에 접속을 할것입니다 성현좌 왈
			//쿼리 준비
			String sql = " SELECT COUNT(*) FROM BOARD ";
			pstmt = con.prepareStatement(sql);
			System.out.println("1/6 getAllCount success");
			//쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			// 단일 행 함수 이기때문에 while문을 돌릴 필요가없다
			if(rs.next()) {	//데이터가 있다면
				count = rs.getInt(1);
			}
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	//모든 화면에 보여질 데이터를 10개씩 추출해서 리턴하는 메소드 
	public Vector<BoardBean> getAllBoard(int startRow, int endRow){

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Vector<BoardBean> v = new Vector<>();

		try {
			con = this.getConnection();//나는 디비에 접속을 할것입니다 성현좌 왈
			System.out.println("1/6 getAllBoard success");
			//서브 쿼리 작성 최신글 가져오는 자식들까지합쳐서 10개씩 가져오는 쿼리		ROWNUM을 RNUM 으로 줄임 //글그룹을 기준으로 내림 차순으로 정렬함 최신글이 가장위로 올라감  
			String sql =  " SELECT * FROM (SELECT A.* ,ROWNUM RNUM FROM (SELECT * FROM BOARD ORDER BY REF DESC ,RE_STEP ASC) A) "
					+ " WHERE RNUM >= ? AND RNUM <= ?"; 	  
			//	RNUM을 기준으로 해서 RNUM이 startRow 크거나 같거나 endRow보다 작은 것의 해당하는것을 가져와라 

			//쿼리 실행할 객체 선언
			pstmt = con.prepareStatement(sql);
			//? 에 값을 대입
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				//데이터를 패키징(가방 = boardBean클래스를 이용해줌)
				BoardBean bean = new BoardBean();
				bean.setSeq(rs.getInt(1));
				bean.setNum(rs.getInt(2));
				System.out.println("1/6 getAllBoard success");
				bean.setWriter(rs.getString(3));
				System.out.println("2/6 getAllBoard success");
				bean.setEmail(rs.getString(4));
				bean.setSubject(rs.getString(5));
				bean.setPassword(rs.getString(6));
				bean.setReg_date(rs.getString(7));
				bean.setRef(rs.getInt(8));
				bean.setRe_step(rs.getInt(9));
				bean.setRe_level(rs.getInt(10));
				bean.setReadcount(rs.getInt(11));
				bean.setContent(rs.getString(12));
				System.out.println("3/6 getAllBoard success");
				//패키징한 데이터를 백터에 저장
				v.add(bean);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	} 

	//하나의 게시글을 저장하는 메소드 호출 

	public void insertBoard(BoardBean bean) {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int ref = 0;
		int re_step=1;	//새글이기에
		int re_level=1;	//새글

		try {
			con = this.getConnection();
			//쿼리 작성
			String refsql = " SELECT MAX(REF) FROM BOARD ";
			pstmt = con.prepareStatement(refsql);
			//쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			if(rs.next()) {
				ref = rs.getInt(1)+1;//가장 큰값에 1을 더해줌
			}
			//데이터를 삽입 하는 쿼리 

			String sql = " INSERT INTO BOARD (SEQ, WRITER, EMAIL, SUBJECT, PASSWORD, REG_DATE, REF, RE_STEP, RE_LEVEL, READCOUNT, CONTENT) "
					+ " VALUES (SEQ_BOARD.NEXTVAL,?, ?, ?, ?, SYSDATE, ?, ?, ?, 0, ? )";
			pstmt = con.prepareStatement(sql);
			// ? 값
			pstmt.setString(1, bean.getWriter());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref);
			pstmt.setInt(6, re_step);
			pstmt.setInt(7, re_level);
			pstmt.setString(8, bean.getContent());

			pstmt.executeUpdate();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//하나의 게시글을 읽어드리는 메소드를 작성 
	public BoardBean getOneBoard(int seq) {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		BoardBean bean = null;

		try {
			con = this.getConnection();
			//하나의 게시글을 읽었다는 조회수 증가
			String countsql = " UPDATE BOARD SET READCOUNT = READCOUNT+1 WHERE seq=?";
			pstmt = con.prepareStatement(countsql);
			pstmt.setInt(1, seq);
			//쿼리 실행
			pstmt.executeUpdate();

			//한 게시글에 대한 정보를 리턴해주는 쿼리를 작성
			String sql =  " SELECT * FROM BOARD WHERE seq=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, seq);
			//쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			if(rs.next()) { //하나의 게시글이 존재 한다면 
				//데이터를 패키징(가방 = boardBean클래스를 이용해줌)
				bean = new BoardBean();
				bean.setSeq(rs.getInt(1));
				bean.setNum(rs.getInt(2));
				System.out.println("1/6 getOneBoard success");
				bean.setWriter(rs.getString(3));
				System.out.println("2/6 getOneBoard success");
				bean.setEmail(rs.getString(4));
				bean.setSubject(rs.getString(5));
				bean.setPassword(rs.getString(6));
				bean.setReg_date(rs.getString(7));
				bean.setRef(rs.getInt(8));
				bean.setRe_step(rs.getInt(9));
				bean.setRe_level(rs.getInt(10));
				bean.setReadcount(rs.getInt(11));
				bean.setContent(rs.getString(12));
				System.out.println("3/6 getOneBoard success");
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	//답변글을 저장하는 메소드 
	public void reInsertBoard(BoardBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int ref = bean.getRef();
		int re_step= bean.getRe_step();	//새글이기에
		int re_level= bean.getRe_level();	//새글

		try {
			con = this.getConnection();
			//핵심 코드  																쿼리문 : AND   RE_LEVEL보다 큰애들보다 큰 애들만 1씩 증가 
			String levelsql = " UPDATE BOARD SET RE_LEVEL= RE_LEVEL+1 WHERE REF=? AND RE_LEVEL >? ";
			pstmt = con.prepareStatement(levelsql);
			pstmt.setInt(1, ref);
			pstmt.setInt(2, re_level);//나보다 큰애들을 1씩증가시킴  내가 최신순으로 나오게 해줌
			//쿼리 실행후 결과를 리턴
			pstmt.executeUpdate();

			//데이터를 삽입 하는 쿼리 

			String sql = " INSERT INTO BOARD (SEQ, WRITER, EMAIL, SUBJECT, PASSWORD, REG_DATE, REF, RE_STEP, RE_LEVEL, READCOUNT, CONTENT) "
					+ " VALUES (SEQ_BOARD.NEXTVAL,?, ?, ?, ?, SYSDATE, ?, ?, ?, 0, ? )";
			pstmt = con.prepareStatement(sql);
			// ? 값
			pstmt.setString(1, bean.getWriter());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref);
			pstmt.setInt(6, re_step+1);		//기존 부모글에 step보다 1을 증가시켜야됨
			pstmt.setInt(7, re_level+1);	//기존 부모글에 step보다 1을 증가시켜야됨
			pstmt.setString(8, bean.getContent());

			pstmt.executeUpdate();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//조회수를 증가하지 않는 하나의 게시글을 리턴하는 메소드
	public BoardBean getoneUpdateBoard(int seq) {		//seq기준으로 정보를 가져옴
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		BoardBean bean = null;

		try {
			con = this.getConnection();
			//한 게시글에 대한 정보를 리턴해주는 쿼리를 작성
			String sql =  " SELECT * FROM BOARD WHERE seq=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, seq);
			//쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			if(rs.next()) { //하나의 게시글이 존재 한다면 
				//데이터를 패키징(가방 = boardBean클래스를 이용해줌)
				bean = new BoardBean();
				bean.setSeq(rs.getInt(1));
				bean.setNum(rs.getInt(2));
				System.out.println("1/6 getOneBoard success");
				bean.setWriter(rs.getString(3));
				System.out.println("2/6 getOneBoard success");
				bean.setEmail(rs.getString(4));
				bean.setSubject(rs.getString(5));
				bean.setPassword(rs.getString(6));
				bean.setReg_date(rs.getString(7));
				bean.setRef(rs.getInt(8));
				bean.setRe_step(rs.getInt(9));
				bean.setRe_level(rs.getInt(10));
				bean.setReadcount(rs.getInt(11));
				bean.setContent(rs.getString(12));
				System.out.println("3/6 getOneBoard success");
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	//하나의 게시글을 수정하는 메소드
	public void updateBoard(int seq, String subject, String content) {

		//데이터베이스 연결
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = this.getConnection();
			//3.쿼리준비 쿼리 실행할 객체 선언
			String sql = " UPDATE BOARD SET SUBJECT=?, CONTENT=? WHERE SEQ=? ";
			pstmt = con.prepareStatement(sql);
			//? 에 값을 대입
			pstmt.setString(1, subject);
			pstmt.setString(2, content);
			pstmt.setInt(3, seq);
			//4.쿼리 실행
			pstmt.executeUpdate();
			//5.자원 반납
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//하나의 게시글을 삭제하는 메소드

	public void deleteBoard(int seq) {

		//데이터베이스 연결
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		try {
			con = this.getConnection();
			//3
			String sql=" DELETE FROM BOARD WHERE SEQ=? ";
			pstmt = con.prepareStatement(sql);
			//?
			pstmt.setInt(1, seq);
			//4
			pstmt.executeUpdate();
			//5
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
















	public Connection getConnection()throws SQLException {
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String user = "hr";
		String password = "hr";

		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}

	public void close(Connection conn, PreparedStatement psmt, ResultSet rs) {		
		try {
			if(conn != null) {
				conn.close();
			}
			if(psmt != null) {
				psmt.close();
			}
			if(rs != null) {
				rs.close();
			}				
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}


}
