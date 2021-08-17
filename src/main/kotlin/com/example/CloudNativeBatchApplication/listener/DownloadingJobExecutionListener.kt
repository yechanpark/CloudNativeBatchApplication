package com.example.CloudNativeBatchApplication.listener

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.StreamUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadingJobExecutionListener: JobExecutionListenerSupport() {
    @Autowired
    private lateinit var s3client: AmazonS3

    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucketName: String

    override fun beforeJob(jobExecution: JobExecution) {
        try {
            val listObjectsRequest = ListObjectsRequest()
            listObjectsRequest.bucketName = bucketName
            listObjectsRequest.prefix = "inputs"

            var objectListing: ObjectListing

            val paths = StringBuilder()

            do {
                objectListing = s3client.listObjects(listObjectsRequest)

                // S3에 존재하는 모든 파일 조회(폴더 포함)
                objectListing.objectSummaries.forEach {
                    if ( !it.key.contains("csv") ) return@forEach

                    val file = File.createTempFile("input", ".csv")
                    val obj = s3client.getObject(GetObjectRequest(bucketName, it.key))

                    StreamUtils.copy(obj.objectContent, FileOutputStream(file))

                    paths.append(file.absolutePath + ",")
                    // 파일 다운로드 절대경로
                    println(">> downloaded file : ${file.absolutePath}")
                }

                listObjectsRequest.marker = objectListing.nextMarker
            } while ( objectListing.isTruncated )

            jobExecution.executionContext.put("localFiles", paths.substring(0, paths.length - 1))
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }
}