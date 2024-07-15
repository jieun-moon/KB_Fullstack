-- USE: 기본 데이터베이스 설정
USE sqldb;

DROP TABLE IF EXISTS stdclubtbl;
DROP TABLE IF EXISTS clubtbl;
DROP TABLE IF EXISTS stdtbl;

-- CREATE TABLE: 테이블 생성
CREATE TABLE stdtbl (
stdName VARCHAR(10) NOT NULL PRIMARY KEY,
addr CHAR(4) NOT NULL
);
-- NOT NULL: NULL이 될 수 없다
-- PRIMARY KEY: 기본키(식별자), 중복되지 않고 NULL이 될 수 없다
CREATE TABLE clubtbl (
clubName VARCHAR(10) NOT NULL PRIMARY KEY,
roomNo CHAR(4) NOT NULL
);

CREATE TABLE stdclubtbl(
num int AUTO_INCREMENT NOT NULL PRIMARY KEY,
stdName VARCHAR(10) NOT NULL,
clubName VARCHAR(10) NOT NULL,
FOREIGN KEY (stdName) REFERENCES stdtbl(stdName),
FOREIGN KEY(clubName) REFERENCES clubtbl(clubName)
);

-- INSERT INTO 테이블명 VALUES 실제 값들: 테이블의 컬럼들에 실제 값들을 추가함(행 추가)
INSERT INTO stdtbl VALUES ('김범수','경남'), ('성시경','서울'), ('조용필','경기'), ('은지원','경북'),('바비킴','서울');
INSERT INTO clubtbl VALUES ('수영', '101호'), ('바둑','102호'), ('축구','103호'), ('봉사','104호');
INSERT INTO stdclubtbl
VALUES (NULL, '김범수','바둑'), (NULL,'김범수','축구'), (NULL,'조용필','축구'),
(NULL, '은지원','축구'), (NULL,'은지원','봉사'), (NULL,'바비킴','봉사');



-- 학생 테이블, 동아리 테이블, 학생 동아리 테이블을 이용해서 학생 기준으로 학생 이름/지역/가입한 동아리방을 출력하세요
SELECT s.stdname, s.addr, sc.clubName, c.roomNo
FROM stdtbl s INNER JOIN stdclubtbl sc
ON s.stdName = sc.stdName
INNER JOIN clubtbl c
ON c.clubName = sc.clubName
ORDER BY s.stdName;

-- 동아리를 기준으로 가입한 학생의 목록을 출력하세요
-- 출력정보: clubName, roomNo, stdName, addr
SELECT c.clubNAme, c.roomNo, s.stdName, s.addr
FROM stdtbl s INNER JOIN stdclubtbl sc
ON s.stdName = sc.stdName
INNER JOIN clubtbl c
ON c.clubName = sc.clubName
ORDER BY c.clubName;


USE sqldb;
CREATE TABLE empTbl(emp CHAR(3), manager CHAR(3), empTel VARCHAR(8));

INSERT INTO empTbl VALUES('나사장', NULL, '0000');
INSERT INTO empTbl VALUES('김재무', '나사장', '2222');
INSERT INTO empTbl VALUES('김부장', '김재무', '2222-1');
INSERT INTO empTbl VALUES('이부장', '김재무', '2222-2');
INSERT INTO empTbl VALUES('우대리', '이부장', '2222-2-1');
INSERT INTO empTbl VALUES('지사원', '이부장', '2222-2-2');
INSERT INTO empTbl VALUES('이영업', '나사장', '1111');
INSERT INTO empTbl VALUES('한과장', '이영업', '1111-1');
INSERT INTO empTbl VALUES('최정보', '나사장', '3333');
INSERT INTO empTbl VALUES('윤차장', '최정보', '3333-1');
INSERT INTO empTbl VALUES('이주임', '윤차장', '3333-1-1');

-- 앞에서 추가한 테이블에서 우대리의 상관 연락처 정보를 확인하세요
-- 동일한 테이블을 두 개 만들어서 JOIN 시킴 => SELF JOIN
SELECT a.emp AS '부하직원', b.emp AS '직속상관', b.empTel AS '직속상관연락처'
FROM emptbl a INNER JOIN emptbl b
ON a.manager = b.emp
WHERE a.emp = '우대리';
