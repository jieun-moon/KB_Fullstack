-- 기본 DB가 선택 안됐을 경우에는
-- 1) use 데이터베이스명;
-- 2) shopdb.producttbl처럼 사용
-- 3) 데이터베이스명 더블 클릭

-- *: 모두 선택, FROM 뒤에는 테이블명, select 뒤에는 컬럼(열)
-- 데이터를 두개 이상 검색할 때는 ,로 이어준다
-- Where 절 뒤에는 조건식, =이 같다라는 뜻으로 쓰임
SELECT * FROM membertbl;