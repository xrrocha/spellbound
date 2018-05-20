package net.xrrocha.spellbound.xtend

import java.util.List
import net.xrrocha.spellbound.xtend.SpellingCorrector.WordSplit
import org.junit.Test

import static junit.framework.TestCase.fail
import static net.xrrocha.spellbound.xtend.SpellingCorrector.LETTERS
import static net.xrrocha.spellbound.xtend.SpellingCorrector.deletes
import static net.xrrocha.spellbound.xtend.SpellingCorrector.inserts
import static net.xrrocha.spellbound.xtend.SpellingCorrector.isAlphabetic
import static net.xrrocha.spellbound.xtend.SpellingCorrector.normalize
import static net.xrrocha.spellbound.xtend.SpellingCorrector.replaces
import static net.xrrocha.spellbound.xtend.SpellingCorrector.splits
import static net.xrrocha.spellbound.xtend.SpellingCorrector.transposes
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static java.util.stream.Collectors.toList

class SpellingCorrectorTest {

  val spellingCorrector = new SpellingCorrector(#{
    'centry' -> 12463,
    'contra' -> 93053,
    'country' -> 105902,
    'ricksha' -> 0,
    'sleeping' -> 101079,
    'sliping' -> 0,
    'sloping' -> 79015,
    'spelling' -> 98993,
    'spewing' -> 64515,
    'spiling' -> 0
  })

  @Test
  def void yieldsEmptyOnDictionaryWord() {
    assertFalse(spellingCorrector.getCorrections('spelling').isPresent)
  }

  @Test
  def void yieldsCorrectionsOnOneTypo() {
    val expectedCorrections = #[
      'spelling', 'spewing', 'spiling'
    ]
    val actualCorrections = spellingCorrector.getCorrections('speling')
    assertTrue(actualCorrections.isPresent)
    assertEquals(expectedCorrections, actualCorrections.get)
  }

  @Test
  def void yieldsCorrectionsOnTwoTypos() {
    val typo2 = "spelinmg";
    val expectedCorrections = List.of(
        "spelling", "spewing", "spiling"
    );

    val actualCorrections = spellingCorrector.getCorrections(typo2);

    assertTrue(actualCorrections.isPresent());
    assertEquals(actualCorrections.get(), expectedCorrections);
  }

  @Test
  def void yieldsNoCorrectionsOnGibberish() {
    val corrections = spellingCorrector.getCorrections('xwphjwl')
    assertTrue(corrections.isPresent)
    assertTrue(corrections.get.isEmpty)
  }

  @Test
  def void buildsSplitsCorrectly() {
    val name = 'dilbert'

    val expectedSplits = #[
      new WordSplit('', 'dilbert'),
      new WordSplit('d', 'ilbert'),
      new WordSplit('di', 'lbert'),
      new WordSplit('dil', 'bert'),
      new WordSplit('dilb', 'ert'),
      new WordSplit('dilbe', 'rt'),
      new WordSplit('dilber', 't'),
      new WordSplit('dilbert', '')
    ]

    val actualSplits = splits(name)

    assertEquals(name.length + 1, actualSplits.size)

    assertEquals(expectedSplits, actualSplits.toList)
  }

  @Test
  def void buildsDeletesCorrectly() {
    val name = 'wally'
    val splits = splits(name)

    val expectedDeletes = #[
      'ally', 'wlly', 'waly', 'waly', 'wall'
    ]
    val actualDeletes = deletes(splits).collect(toList)
    assertEquals(actualDeletes.size, name.length)

    assertEquals(expectedDeletes, actualDeletes)
  }

  @Test
  def void buildsTransposesCorrectly() {
    val name = 'alice'
    val splits = splits(name)

    val expectedTransposes = #[
      'laice',
      'ailce',
      'alcie',
      'aliec'
    ]
    val actualTransposes = transposes(splits).collect(toList)
    assertEquals(actualTransposes.size, name.length - 1)

    assertEquals(expectedTransposes, actualTransposes)
  }

  @Test
  def void buildsReplacesCorrectly() {
    val name = 'asok'
    val splits = splits(name)

    val expectedReplaces = #[
        'asok', 'bsok', 'csok', 'dsok', 'esok', 'fsok', 'gsok', 'hsok',
        'isok', 'jsok', 'ksok', 'lsok', 'msok', 'nsok', 'osok', 'psok',
        'qsok', 'rsok', 'ssok', 'tsok', 'usok', 'vsok', 'wsok', 'xsok',
        'ysok', 'zsok', 'aaok', 'abok', 'acok', 'adok', 'aeok', 'afok',
        'agok', 'ahok', 'aiok', 'ajok', 'akok', 'alok', 'amok', 'anok',
        'aook', 'apok', 'aqok', 'arok', 'asok', 'atok', 'auok', 'avok',
        'awok', 'axok', 'ayok', 'azok', 'asak', 'asbk', 'asck', 'asdk',
        'asek', 'asfk', 'asgk', 'ashk', 'asik', 'asjk', 'askk', 'aslk',
        'asmk', 'asnk', 'asok', 'aspk', 'asqk', 'asrk', 'assk', 'astk',
        'asuk', 'asvk', 'aswk', 'asxk', 'asyk', 'aszk', 'asoa', 'asob',
        'asoc', 'asod', 'asoe', 'asof', 'asog', 'asoh', 'asoi', 'asoj',
        'asok', 'asol', 'asom', 'ason', 'asoo', 'asop', 'asoq', 'asor',
        'asos', 'asot', 'asou', 'asov', 'asow', 'asox', 'asoy', 'asoz'
    ]
    val actualReplaces = replaces(splits).collect(toList)
    assertEquals(actualReplaces.size, LETTERS.length * name.length)

    assertEquals(expectedReplaces, actualReplaces)
  }

  @Test
  def void buildsInsertsCorrectly() {
    val name = 'dgbert'
    val splits = splits(name)

    val expectedInserts = #[
        'adgbert', 'bdgbert', 'cdgbert', 'ddgbert', 'edgbert', 'fdgbert',
        'gdgbert', 'hdgbert', 'idgbert', 'jdgbert', 'kdgbert', 'ldgbert',
        'mdgbert', 'ndgbert', 'odgbert', 'pdgbert', 'qdgbert', 'rdgbert',
        'sdgbert', 'tdgbert', 'udgbert', 'vdgbert', 'wdgbert', 'xdgbert',
        'ydgbert', 'zdgbert', 'dagbert', 'dbgbert', 'dcgbert', 'ddgbert',
        'degbert', 'dfgbert', 'dggbert', 'dhgbert', 'digbert', 'djgbert',
        'dkgbert', 'dlgbert', 'dmgbert', 'dngbert', 'dogbert', 'dpgbert',
        'dqgbert', 'drgbert', 'dsgbert', 'dtgbert', 'dugbert', 'dvgbert',
        'dwgbert', 'dxgbert', 'dygbert', 'dzgbert', 'dgabert', 'dgbbert',
        'dgcbert', 'dgdbert', 'dgebert', 'dgfbert', 'dggbert', 'dghbert',
        'dgibert', 'dgjbert', 'dgkbert', 'dglbert', 'dgmbert', 'dgnbert',
        'dgobert', 'dgpbert', 'dgqbert', 'dgrbert', 'dgsbert', 'dgtbert',
        'dgubert', 'dgvbert', 'dgwbert', 'dgxbert', 'dgybert', 'dgzbert',
        'dgbaert', 'dgbbert', 'dgbcert', 'dgbdert', 'dgbeert', 'dgbfert',
        'dgbgert', 'dgbhert', 'dgbiert', 'dgbjert', 'dgbkert', 'dgblert',
        'dgbmert', 'dgbnert', 'dgboert', 'dgbpert', 'dgbqert', 'dgbrert',
        'dgbsert', 'dgbtert', 'dgbuert', 'dgbvert', 'dgbwert', 'dgbxert',
        'dgbyert', 'dgbzert', 'dgbeart', 'dgbebrt', 'dgbecrt', 'dgbedrt',
        'dgbeert', 'dgbefrt', 'dgbegrt', 'dgbehrt', 'dgbeirt', 'dgbejrt',
        'dgbekrt', 'dgbelrt', 'dgbemrt', 'dgbenrt', 'dgbeort', 'dgbeprt',
        'dgbeqrt', 'dgberrt', 'dgbesrt', 'dgbetrt', 'dgbeurt', 'dgbevrt',
        'dgbewrt', 'dgbexrt', 'dgbeyrt', 'dgbezrt', 'dgberat', 'dgberbt',
        'dgberct', 'dgberdt', 'dgberet', 'dgberft', 'dgbergt', 'dgberht',
        'dgberit', 'dgberjt', 'dgberkt', 'dgberlt', 'dgbermt', 'dgbernt',
        'dgberot', 'dgberpt', 'dgberqt', 'dgberrt', 'dgberst', 'dgbertt',
        'dgberut', 'dgbervt', 'dgberwt', 'dgberxt', 'dgberyt', 'dgberzt',
        'dgberta', 'dgbertb', 'dgbertc', 'dgbertd', 'dgberte', 'dgbertf',
        'dgbertg', 'dgberth', 'dgberti', 'dgbertj', 'dgbertk', 'dgbertl',
        'dgbertm', 'dgbertn', 'dgberto', 'dgbertp', 'dgbertq', 'dgbertr',
        'dgberts', 'dgbertt', 'dgbertu', 'dgbertv', 'dgbertw', 'dgbertx',
        'dgberty', 'dgbertz'
    ]
    val actualInserts = inserts(splits).collect(toList)
    assertEquals(actualInserts.size, LETTERS.length * (name.length + 1))

    assertEquals(expectedInserts, actualInserts)
  }

  @Test(expected=NullPointerException)
  def void rejectsNullAlphabetic() {
    isAlphabetic(null)
  }

  @Test
  def void recognizesNonAlphabetic() {
    assertFalse(isAlphabetic('neo42'))
  }

  @Test
  def void acceptsAlphabetic() {
    isAlphabetic('neo')
    isAlphabetic('Neo')
    isAlphabetic('NEO')
  }

  @Test(expected=NullPointerException)
  def void rejectsNullWord() {
    spellingCorrector.getCorrections(null)
  }

  @Test(expected=NullPointerException)
  def void rejectsNullNormalization() {
    normalize(null)
  }

  @Test(expected=IllegalArgumentException)
  def void rejectsNonAlphaNormalization() {
    normalize('!@#$')
  }

  @Test
  def void acceptsAlphaNormalization() {
    val normalizedWord = normalize(' Neo\t\n')
    assertEquals('neo', normalizedWord)
  }

  @Test(expected=NullPointerException)
  def void rejectsNullDictionary() {
    new SpellingCorrector(null)
  }

  @Test(expected=IllegalArgumentException)
  def void rejectsEmptyDictionary() {
    new SpellingCorrector(emptyMap)
  }

  @Test
  def void rejectsInvalidWords() {
    val nonAlphas = #[
      '',
      ' \t\n',
      '123',
      'number42'
    ]
    nonAlphas.forEach [ badWord |
      try {
        spellingCorrector.getCorrections(badWord)
        fail('Accepts non-alpha: ' + badWord)
      } catch (Exception e) {
      }
    ]
  }

  @Test(expected=NullPointerException)
  def void rejectsNullWords() {
    spellingCorrector.getCorrections(null)
  }

  static def <T> boolean equals(List<T> s1, List<T> s2) {
    assertEquals(s1.size, s2.size)
    (0 .. s1.size).forall[s1.get(it) == s2.get(it)]
  }
}
