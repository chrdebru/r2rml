-- phpMyAdmin SQL Dump
-- version 4.2.10
-- http://www.phpmyadmin.net
--
-- Host: localhost:3306
-- Generation Time: May 10, 2016 at 08:47 PM
-- Server version: 5.5.38
-- PHP Version: 5.6.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `r2rml`
--

-- --------------------------------------------------------

--
-- Table structure for table `DEPT`
--

CREATE TABLE `DEPT` (
  `DEPTNO` int(11) NOT NULL,
  `DNAME` varchar(30) DEFAULT NULL,
  `LOC` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `DEPT`
--

INSERT INTO `DEPT` (`DEPTNO`, `DNAME`, `LOC`) VALUES
(10, 'APPSERVER', 'NEW YORK');

-- --------------------------------------------------------

--
-- Table structure for table `DEPT2`
--

CREATE TABLE `DEPT2` (
  `DEPTNO` int(11) DEFAULT NULL,
  `DNAME` varchar(30) DEFAULT NULL,
  `LOC` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `DEPT2`
--

INSERT INTO `DEPT2` (`DEPTNO`, `DNAME`, `LOC`) VALUES
(10, 'APPSERVER', 'NEW YORK'),
(20, 'RESEARCH', 'BOSTON');

-- --------------------------------------------------------

--
-- Table structure for table `EMP`
--

CREATE TABLE `EMP` (
  `EMPNO` int(11) NOT NULL,
  `ENAME` varchar(100) DEFAULT NULL,
  `JOB` varchar(20) DEFAULT NULL,
  `DEPTNO` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `EMP`
--

INSERT INTO `EMP` (`EMPNO`, `ENAME`, `JOB`, `DEPTNO`) VALUES
(7369, 'SMITH', 'CLERK', 10);

-- --------------------------------------------------------

--
-- Table structure for table `EMP2`
--

CREATE TABLE `EMP2` (
  `EMPNO` int(11) DEFAULT NULL,
  `ENAME` varchar(100) DEFAULT NULL,
  `JOB` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `EMP2`
--

INSERT INTO `EMP2` (`EMPNO`, `ENAME`, `JOB`) VALUES
(7369, 'SMITH', 'CLERK'),
(7369, 'SMITH', 'NIGHTGUARD'),
(7400, 'JONES', 'ENGINEER');

-- --------------------------------------------------------

--
-- Table structure for table `EMP2DEPT`
--

CREATE TABLE `EMP2DEPT` (
  `EMPNO` int(11) DEFAULT NULL,
  `DEPTNO` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `EMP2DEPT`
--

INSERT INTO `EMP2DEPT` (`EMPNO`, `DEPTNO`) VALUES
(7369, 10),
(7369, 20),
(7400, 10);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `DEPT`
--
ALTER TABLE `DEPT`
 ADD PRIMARY KEY (`DEPTNO`);

--
-- Indexes for table `EMP`
--
ALTER TABLE `EMP`
 ADD PRIMARY KEY (`EMPNO`), ADD KEY `DEPTNO` (`DEPTNO`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `EMP`
--
ALTER TABLE `EMP`
ADD CONSTRAINT `emp_ibfk_1` FOREIGN KEY (`DEPTNO`) REFERENCES `DEPT` (`DEPTNO`);
