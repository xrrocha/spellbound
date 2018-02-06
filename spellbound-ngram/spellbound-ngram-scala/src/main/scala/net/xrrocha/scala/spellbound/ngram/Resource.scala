package net.xrrocha.scala.spellbound.ngram

import java.io._
import java.net.{URI, URL, URLConnection, URLStreamHandler}

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.model.{CannedAccessControlList, ObjectMetadata}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.scalalogging.LazyLogging

trait Resource {

  def location: Location

  def inputStream(): InputStream

  def outputStream(): OutputStream

  /**
    * Epoch milliseconds for resource last modification date.
    *
    * @return Resource epoch milliseconds for last modification date
    *         or negative value to indicate resource non-existence
    */
  def lastModified(): Long
}

object Resource {
  val BufferSize = 65536
}

case class FileResource(location: Location) extends Resource with LazyLogging {

  private lazy val file = {
    val file = new File(location)
    if (!file.exists()) {
      file.getParentFile.mkdirs()
    }
    file
  }

  def inputStream(): InputStream = new FileInputStream(file)

  def outputStream(): OutputStream = new FileOutputStream(file)

  def lastModified(): Long =
    if (file.exists()) {
      require(file.isFile && file.canRead)
      file.lastModified()
    } else {
      logger.debug(s"Non-existent file: ${file.getAbsoluteFile}")
      -1L
    }
}

/**
  * Opens an AWS S3 URL returning its <code>InputStream</code>. This class encapsulates server
  * credentials, bucket names, etc.
  */
case class S3Resource(location: Location,
                      region: Regions,
                      credentials: AWSCredentials)
  extends Resource
    with LazyLogging {

  lazy val urlStreamHandler = new S3UrlStreamHandler(region, credentials)

  /**
    * Open an AWS S3 URL returning its <code>InputStream</code>.
    *
    * @return The <code>InputStream</code> containing the AWS S3 URL's contents
    */
  override def inputStream(): InputStream = connection.getInputStream

  /**
    * Open an AWS S3 URL returning its <code>OutputStream</code>.
    *
    * @return The <code>OutputStream</code> to write the AWS S3 file contents
    */
  override def outputStream(): OutputStream = connection.getOutputStream

  override def lastModified(): Long = connection.getLastModified

  private def connection = {
    val connection = url.openConnection()
    connection.setDoOutput(true)
    connection.setUseCaches(false)
    connection
  }

  private def url: URL = {
    val uri = new URI(location)
    new URL(uri.getScheme,
      uri.getHost,
      uri.getPort,
      uri.getPath,
      urlStreamHandler)
  }
}

/**
  * AWS S3-aware implementation of @see{URLStreamHandler}.
  */
class S3UrlStreamHandler(val region: Regions,
                         val credentials: AWSCredentials)
  extends URLStreamHandler
    with LazyLogging {

  private lazy val s3: AmazonS3 = AmazonS3ClientBuilder
    .standard
    .withRegion(region)
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .build

  override protected def openConnection(url: URL): URLConnection = new URLConnection(url) {

    private lazy val bucket = url.getHost

    private lazy val key = url.getPath.substring(1)

    override def getLastModified: Long = {
      if (s3.doesObjectExist(bucket, key)) {
        s3.getObject(bucket, key).getObjectMetadata.getLastModified.getTime
      } else {
        logger.debug(s"Non-existent object: $bucket/$key")
        -1L
      }

    }

    override def getOutputStream: OutputStream = {

      val outputStream = new ByteArrayOutputStream(Resource.BufferSize) {

        override def close(): Unit = {

          flush()
          super.close()

          val byteArray = toByteArray
          val inputStream = new ByteArrayInputStream(byteArray)

          try {
            logger.debug(s"Writing ${byteArray.length} bytes object $key to S3 bucket $bucket")
            val objectMetadata = new ObjectMetadata
            objectMetadata.setContentLength(byteArray.length)
            s3.putObject(bucket, key, inputStream, objectMetadata)
            s3.setObjectAcl(bucket, key, CannedAccessControlList.PublicRead)
          } finally {
            logger.debug(s"Completing writing object $key to S3 bucket $bucket")
            inputStream.close()
            logger.debug(s"Done writing object $key to S3 bucket $bucket")
          }
        }
      }


      //      val inputStream = new PipedInputStream()
      //      val outputStream = new PipedOutputStream(inputStream)
      //
      //      new Thread(() => {
      //        logger.debug(s"Writing object $key to S3 bucket $bucket")
      //        val objectMetadata = new ObjectMetadata
      //        s3.putObject(bucket, key, inputStream, objectMetadata)
      //        s3.setObjectAcl(bucket, key, CannedAccessControlList.PublicRead)
      //        inputStream.close()
      //      }).start()
      //
      //      Thread.sleep(2048L)

      outputStream
    }

    override def getInputStream: InputStream =
      s3.getObject(bucket, key).getObjectContent

    override def connect(): Unit = {}
  }
}
