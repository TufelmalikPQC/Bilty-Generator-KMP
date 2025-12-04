package com.bilty.generator.model.constants

object FirebaseConstants {
   const val APPLICATION_ID = "1:544145711438:android:1da4d42b98055d95ea3e1f"
   const val REALTIME_DATABASE_URL = "https://bilty-generator-default-rtdb.firebaseio.com"
   const val PROJECT_ID = "bilty-generator"
   const val API_KEY = "AIzaSyBRgeLGe5CuGhMZqQRVNor__-wEbCYxtQY"
   const val GCM_SENDER_ID = "544145711438"

   /**
    * Firebase Realtime Database node names and path components
    * Centralized constants to avoid hardcoded strings
    */
   object Nodes {
       /**
        * Print queue system nodes (dual-node structure)
        */
       object PrintQueue {
           /** Full print job data: printJobs/{fbNodeId} */
           const val PRINT_JOBS = "printJobs"
           
           /** Lightweight index: printIndex/company_{id}/branch_{id}/{grNo} */
           const val PRINT_INDEX = "printIndex"
       }
       
       /**
        * Remote print request nodes (legacy/existing system)
        */
       object RemotePrint {
           /** Print requests path segment */
           const val PRINT = "print"
       }
       
       /**
        * Path prefixes for organization hierarchy
        * Usage: "${Prefixes.COMPANY}${companyId}"
        */
       object Prefixes {
           const val COMPANY = "company_"
           const val BRANCH = "branch_"
       }
       
       /**
        * Child node field names
        */
       object Fields {
           const val STATUS = "status"
       }
   }
}