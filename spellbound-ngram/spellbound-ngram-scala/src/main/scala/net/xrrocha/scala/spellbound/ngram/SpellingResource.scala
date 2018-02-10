package net.xrrocha.scala.spellbound.ngram

import java.io.{BufferedOutputStream, InputStream, OutputStream}

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions

import scala.io.Source

trait SpellingResource {

  // Configuration: ngram length
  def ngramLength: Int

  // Dependency: directory resource
  def dictionaryResource: Resource

  // Dependency: ngram map resource
  def ngramResource: Resource

  // Movable part: how to normalize words prior to n-gram extraction
  def normalize(word: Word): NormalizedWord =
    SpellingResource.normalize(word: Word)

  // Movable part: how to extract n-grams
  def ngrams(word: NormalizedWord): Set[NGram] =
    SpellingResource.ngrams(word, ngramLength)(normalize)

  // Configuration: should we persist ngram map resource on rebuild?
  def saveNGramResource: Boolean = true

  // Workhorse #1: retrieve dictionary from resource
  lazy val dictionary: Map[NormalizedWord, Seq[(Word, Rank)]] =
    SpellingResource.loadDictionary(dictionaryResource)

  // Workhorse #2: retrieve ngram map from resource
  lazy val ngramMap: Map[NGram, Set[NormalizedWord]] =

    if (ngramResource.lastModified() > dictionaryResource.lastModified()) {
      // Case #1: Rebuild stale ngram map after dictionary dependency modification
      SpellingResource.loadNGramMap(ngramResource.inputStream())
    } else {
      // Case #2: Simply load current ngram map from resource
      val ngramMap =
        SpellingResource.buildNGramMap(dictionary, ngramLength)

      // Persist ngram map result so as to speed initialization up on next run
      if (saveNGramResource) {
        SpellingResource.saveNGramMap(ngramMap, ngramResource.outputStream())
      }

      ngramMap
    }
}

object SpellingResource {

  // Normalize words to ensure proper lookup
  def normalize(word: Word): NormalizedWord =
    word.trim.toLowerCase

  // Default ngram extraction implementation
  def ngrams(word: String, ngramLength: Int = 3)
            (implicit normalize: Word => NormalizedWord = SpellingResource.normalize)
  : Set[NGram] =
    normalize(word)
      .split("\\s+")
      .flatMap(_.sliding(ngramLength))
      .toSet

  // Load existing dictionary from resource
  def loadDictionary(dictionaryResource: Resource): Map[NormalizedWord, Seq[(Word, Rank)]] =
  // Open file from ngramResource
    Source.fromInputStream(dictionaryResource.inputStream())
      // Each line contains a separate word
      .getLines()
      // Normalize each word
      .map { line =>
      val Array(word, rank) = line.split("\\t")
      (normalize(word), word, rank.toLong)
    }
      // Make each normalized word point to its originating word(s)
      .toSeq
      .groupBy { case (normalizedWord, _, _) => normalizedWord }
      .mapValues {
        _.map { case (_, word, rank) => (word, rank) }.distinct
      }

  // Load existing ngram map from resource
  def loadNGramMap(in: InputStream): Map[NormalizedWord, Set[Word]] =
  // ngram map modified more recently than dictionary
    Source.fromInputStream(in)
      // Read file line-by-line
      .getLines()
      // Extract distinct ngrams from each word and flatten nested list: Seq[(NGram,Word)]
      .map { line =>
      // First field: ngram
      val Array(ngram, words) = line.split("\\t")
      // Second field: comma-separated list of associated words
      val wordArray = words.split(",")
      // Produce pair of ngram and word
      ngram -> wordArray.toSet
    }
      .toMap

  // Build ngram map from a dictionary given an n-gram length
  def buildNGramMap(dictionary: Map[NormalizedWord, Seq[(Word, Rank)]],
                    ngramLength: Length): Map[NGram, Set[NormalizedWord]] = {
    val ngram2words =
    // Extract normalized words for grouping below: Iterable[Word]
      dictionary.keySet
        // Extract multiple (but distinct) ngrams in word. Flatten nested seq: Seq[(NGram, Word)]
        .flatMap { normalizedWord =>
        ngrams(normalizedWord, ngramLength)(identity).map(ngram => (ngram, normalizedWord))
      }
        // Build map with ngrams as keys and ngram/word pairs as values: Map[NGram, Seq[(NGram,
        // Word)]
        .groupBy { case (ngram, _) => ngram }
        // Transform ngram/word value pairs to remove the and leave only sorted word
        // list: Map[NGram, Seq[Word]]
        .mapValues { pairs => pairs.map { case (_, word) => word } }

    ngram2words
  }

  // Actually save n-gram map onto resource if configured as persistent
  def saveNGramMap(ngramMap: Map[NGram, Set[NormalizedWord]],
                   outputStream: OutputStream): Unit = {

    // Persis n-gram map on a separate thread so as to make spelling service available ASAP
    runUntilCompletion {
      val out = new BufferedOutputStream(outputStream, Resource.BufferSize)
      ngramMap.foreach { case (ngram, words) =>
        out.write(s"$ngram\t${words.mkString(",")}\n".getBytes())
      }
      out.flush()
      out.close()
    }
  }
}

// Config-based configuration and dependency resolution for SpellingResource
trait SpellingResourceConfig extends ConfigSettings {
  self: SpellingResource =>

  lazy val configName = "spellingResources"

  lazy val ngramLength: Int = config.getInt("ngramLength")

  lazy val dictionaryLocation: Location = config.getString("dictionaryLocation")
  lazy val ngramLocation: Location = config.getString("ngramLocation")

  lazy val resourceType: String = config.getString("resourceType")
  override lazy val (dictionaryResource, ngramResource) = resourceType match {
    case "file" =>
      (FileResource(dictionaryLocation), FileResource(ngramLocation))
    case "s3" =>
      val region: Regions = Regions.fromName(config.getString("region"))
      // Credentials drawn from ~/.aws/credentials
      val credentials: AWSCredentials = new ProfileCredentialsProvider().getCredentials
      val dictionaryResource = S3Resource(dictionaryLocation, region, credentials)
      val ngramResource = S3Resource(ngramLocation, region, credentials)
      (dictionaryResource, ngramResource)
    case _ =>
      throw new IllegalArgumentException(s"No such resource type: $resourceType")
  }
}