package net.xrrocha.scala.spellbound.ngram

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSuite

trait BaseTestSuggester extends DefaultSpellingSuggester {
  lazy val stringMetric: StringMetric = DamerauStringMetric
}

trait BaseSpellingResource extends SpellingResource {
  lazy val ngramLength = 3
}

class FileSpellingSuggesterTest extends DefaultSpellingSuggesterTest {
  lazy val suggester: DefaultSpellingSuggester = new BaseTestSuggester {
    lazy val spellingResource: SpellingResource = new BaseSpellingResource {
      lazy val dictionaryResource = FileResource("../data/dictionary.tsv")
      lazy val ngramResource = FileResource("../data/ngrams.tsv")
    }
  }
}

class S3SpellingSuggesterTest extends DefaultSpellingSuggesterTest {
  lazy val suggester: DefaultSpellingSuggester = new BaseTestSuggester {
    lazy val spellingResource: SpellingResource = new BaseSpellingResource {
      lazy val region = Regions.US_EAST_1
      lazy val credentials: AWSCredentials =
        new ProfileCredentialsProvider().getCredentials
      lazy val dictionaryResource =
        S3Resource("s3://spellbound.xrrocha.net/dictionary.tsv", region, credentials)
      lazy val ngramResource =
        S3Resource("s3://spellbound.xrrocha.net/ngrams.tsv", region, credentials)
    }
  }
}

trait DefaultSpellingSuggesterTest extends FunSuite with LazyLogging {

  def suggester: DefaultSpellingSuggester

  test("Returns no suggestions on dictionary word") {
    assert(suggester.getSuggestions("senselessness").isEmpty)
  }

  test("Returns appropriate suggestions") {
    val ricshaSuggestions = suggester.getSuggestions("ricsha")
    assert(ricshaSuggestions.isDefined)
    assert(ricshaSuggestions.get == Seq("richa", "rickshaw"))

    val slepingSuggestions = suggester.getSuggestions("sleping")
    assert(slepingSuggestions.isDefined)
    assert(slepingSuggestions.get ==
      Seq("sleeping", "seeping", "slewing", "sloping", "slopping", "sledging", "steeping",
        "sleepinn", "slumping", "bleeping", "sleeving", "slurping", "slapping", "sledding",
        "slipping", "sweeping", "stepping"))
  }

  test("Returns defined but empty suggestion list on no matching ngram") {
    val emptySuggestions = suggester.getSuggestions("dftba")
    assert(emptySuggestions.isDefined)
    assert(emptySuggestions.get.isEmpty)
  }

  test("Returns words shorter than ngramLength") {
    assert(suggester.spellingResource.ngramMap.contains("as"))
  }
}
