-- phpMyAdmin SQL Dump
-- version 4.6.6deb4
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 17, 2018 at 10:10 AM
-- Server version: 10.1.26-MariaDB-0+deb9u1
-- PHP Version: 7.0.27-0+deb9u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `query_store`
--

-- --------------------------------------------------------

--
-- Table structure for table `AuthorizedFields`
--

CREATE TABLE `AuthorizedFields` (
  `fieldNames` text COLLATE utf8_unicode_ci NOT NULL,
  `authorizedValues` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `AuthorizedParameters`
--

CREATE TABLE `AuthorizedParameters` (
  `paramName` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Errors`
--

CREATE TABLE `Errors` (
  `UUID` text COLLATE utf8_unicode_ci,
  `Token` text COLLATE utf8_unicode_ci,
  `ErrorMessage` text COLLATE utf8_unicode_ci,
  `Parameters` text COLLATE utf8_unicode_ci,
  `Timestamp` text COLLATE utf8_unicode_ci,
  `Phase` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Notifications`
--

CREATE TABLE `Notifications` (
  `timestamp` text COLLATE utf8_unicode_ci NOT NULL,
  `queryToken` text COLLATE utf8_unicode_ci NOT NULL,
  `notifierIP` text COLLATE utf8_unicode_ci NOT NULL,
  `accededResource` text COLLATE utf8_unicode_ci NOT NULL,
  `resourceVersion` text COLLATE utf8_unicode_ci,
  `userEmail` text COLLATE utf8_unicode_ci,
  `usedClient` text COLLATE utf8_unicode_ci,
  `accessType` text COLLATE utf8_unicode_ci,
  `outputFormatVersion` text COLLATE utf8_unicode_ci,
  `dataURL` text COLLATE utf8_unicode_ci,
  `parameters` text COLLATE utf8_unicode_ci
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Queries`
--

CREATE TABLE `Queries` (
  `UUID` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `accededResource` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `resourceVersion` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `outputFormatVersion` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `dataURL` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `canonicalParameters` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `queryRexecutionLink` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `biblioGraphicReferences` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `Doi` varchar(200) DEFAULT NULL,
  `DoiSubmitId` varchar(200) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `QueryUserLink`
--

CREATE TABLE `QueryUserLink` (
  `UUID` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `OriginalParameters` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `userEmail` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `userClient` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `notifierIP` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `queryToken` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `TechConfig`
--

CREATE TABLE `TechConfig` (
  `ServletContainerAddress` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `AbsoluteDataPath` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `AbsoluteConfigPath` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `secret` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Queries`
--
ALTER TABLE `Queries`
  ADD PRIMARY KEY (`UUID`),
  ADD UNIQUE KEY `UUID` (`UUID`);

--
-- Indexes for table `TechConfig`
--
ALTER TABLE `TechConfig`
  ADD PRIMARY KEY (`AbsoluteDataPath`(50));

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
