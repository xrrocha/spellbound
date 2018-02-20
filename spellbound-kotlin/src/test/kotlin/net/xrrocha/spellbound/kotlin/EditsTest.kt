package net.xrrocha.spellbound.kotlin

import net.xrrocha.spellbound.kotlin.Edits.ALL_EDITS
import net.xrrocha.spellbound.kotlin.Edits.WordSplit
import net.xrrocha.spellbound.kotlin.Edits.wordSplits
import org.junit.Test
import kotlin.test.assertEquals

class EditsTest {

    private val dilbertWordSplits = listOf(
            WordSplit("", "dilbert"),
            WordSplit("d", "ilbert"),
            WordSplit("di", "lbert"),
            WordSplit("dil", "bert"),
            WordSplit("dilb", "ert"),
            WordSplit("dilbe", "rt"),
            WordSplit("dilber", "t"),
            WordSplit("dilbert", ""))

    @Test
    fun splitsWordsCorrectly() {
        assertEquals(
                dilbertWordSplits,
                wordSplits("dilbert")
        )
    }

    @Test
    fun deletesWordsCorrectly() {
        val deletes = ALL_EDITS[0]
        assertEquals(
                listOf("ilbert",
                        "dlbert",
                        "dibert",
                        "dilert",
                        "dilbrt",
                        "dilbet",
                        "dilber"),
                deletes(dilbertWordSplits)
        )
    }

    @Test
    fun insertWordsCorrectly() {
        val inserts = ALL_EDITS[1]
        assertEquals(
                listOf("adilbert", "bdilbert", "cdilbert", "ddilbert",
                        "edilbert", "fdilbert", "gdilbert", "hdilbert",
                        "idilbert", "jdilbert", "kdilbert", "ldilbert",
                        "mdilbert", "ndilbert", "odilbert", "pdilbert",
                        "qdilbert", "rdilbert", "sdilbert", "tdilbert",
                        "udilbert", "vdilbert", "wdilbert", "xdilbert",
                        "ydilbert", "zdilbert", "dailbert", "dbilbert",
                        "dcilbert", "ddilbert", "deilbert", "dfilbert",
                        "dgilbert", "dhilbert", "diilbert", "djilbert",
                        "dkilbert", "dlilbert", "dmilbert", "dnilbert",
                        "doilbert", "dpilbert", "dqilbert", "drilbert",
                        "dsilbert", "dtilbert", "duilbert", "dvilbert",
                        "dwilbert", "dxilbert", "dyilbert", "dzilbert",
                        "dialbert", "diblbert", "diclbert", "didlbert",
                        "dielbert", "diflbert", "diglbert", "dihlbert",
                        "diilbert", "dijlbert", "diklbert", "dillbert",
                        "dimlbert", "dinlbert", "diolbert", "diplbert",
                        "diqlbert", "dirlbert", "dislbert", "ditlbert",
                        "diulbert", "divlbert", "diwlbert", "dixlbert",
                        "diylbert", "dizlbert", "dilabert", "dilbbert",
                        "dilcbert", "dildbert", "dilebert", "dilfbert",
                        "dilgbert", "dilhbert", "dilibert", "diljbert",
                        "dilkbert", "dillbert", "dilmbert", "dilnbert",
                        "dilobert", "dilpbert", "dilqbert", "dilrbert",
                        "dilsbert", "diltbert", "dilubert", "dilvbert",
                        "dilwbert", "dilxbert", "dilybert", "dilzbert",
                        "dilbaert", "dilbbert", "dilbcert", "dilbdert",
                        "dilbeert", "dilbfert", "dilbgert", "dilbhert",
                        "dilbiert", "dilbjert", "dilbkert", "dilblert",
                        "dilbmert", "dilbnert", "dilboert", "dilbpert",
                        "dilbqert", "dilbrert", "dilbsert", "dilbtert",
                        "dilbuert", "dilbvert", "dilbwert", "dilbxert",
                        "dilbyert", "dilbzert", "dilbeart", "dilbebrt",
                        "dilbecrt", "dilbedrt", "dilbeert", "dilbefrt",
                        "dilbegrt", "dilbehrt", "dilbeirt", "dilbejrt",
                        "dilbekrt", "dilbelrt", "dilbemrt", "dilbenrt",
                        "dilbeort", "dilbeprt", "dilbeqrt", "dilberrt",
                        "dilbesrt", "dilbetrt", "dilbeurt", "dilbevrt",
                        "dilbewrt", "dilbexrt", "dilbeyrt", "dilbezrt",
                        "dilberat", "dilberbt", "dilberct", "dilberdt",
                        "dilberet", "dilberft", "dilbergt", "dilberht",
                        "dilberit", "dilberjt", "dilberkt", "dilberlt",
                        "dilbermt", "dilbernt", "dilberot", "dilberpt",
                        "dilberqt", "dilberrt", "dilberst", "dilbertt",
                        "dilberut", "dilbervt", "dilberwt", "dilberxt",
                        "dilberyt", "dilberzt", "dilberta", "dilbertb",
                        "dilbertc", "dilbertd", "dilberte", "dilbertf",
                        "dilbertg", "dilberth", "dilberti", "dilbertj",
                        "dilbertk", "dilbertl", "dilbertm", "dilbertn",
                        "dilberto", "dilbertp", "dilbertq", "dilbertr",
                        "dilberts", "dilbertt", "dilbertu", "dilbertv",
                        "dilbertw", "dilbertx", "dilberty", "dilbertz"
                ),
                inserts(dilbertWordSplits)
        )
    }

    @Test
    fun transposesWordsCorrectly() {
        val transposes = ALL_EDITS[2]
        assertEquals(
                listOf("idlbert",
                        "dlibert",
                        "diblert",
                        "dilebrt",
                        "dilbret",
                        "dilbetr"),
                transposes(dilbertWordSplits)
        )
    }

    @Test
    fun replacesWordsCorrectly() {
        val replaces = ALL_EDITS[3]
        assertEquals(
                listOf("ailbert", "bilbert", "cilbert", "dilbert",
                        "eilbert", "filbert", "gilbert", "hilbert",
                        "iilbert", "jilbert", "kilbert", "lilbert",
                        "milbert", "nilbert", "oilbert", "pilbert",
                        "qilbert", "rilbert", "silbert", "tilbert",
                        "uilbert", "vilbert", "wilbert", "xilbert",
                        "yilbert", "zilbert", "dalbert", "dblbert",
                        "dclbert", "ddlbert", "delbert", "dflbert",
                        "dglbert", "dhlbert", "dilbert", "djlbert",
                        "dklbert", "dllbert", "dmlbert", "dnlbert",
                        "dolbert", "dplbert", "dqlbert", "drlbert",
                        "dslbert", "dtlbert", "dulbert", "dvlbert",
                        "dwlbert", "dxlbert", "dylbert", "dzlbert",
                        "diabert", "dibbert", "dicbert", "didbert",
                        "diebert", "difbert", "digbert", "dihbert",
                        "diibert", "dijbert", "dikbert", "dilbert",
                        "dimbert", "dinbert", "diobert", "dipbert",
                        "diqbert", "dirbert", "disbert", "ditbert",
                        "diubert", "divbert", "diwbert", "dixbert",
                        "diybert", "dizbert", "dilaert", "dilbert",
                        "dilcert", "dildert", "dileert", "dilfert",
                        "dilgert", "dilhert", "diliert", "diljert",
                        "dilkert", "dillert", "dilmert", "dilnert",
                        "diloert", "dilpert", "dilqert", "dilrert",
                        "dilsert", "diltert", "diluert", "dilvert",
                        "dilwert", "dilxert", "dilyert", "dilzert",
                        "dilbart", "dilbbrt", "dilbcrt", "dilbdrt",
                        "dilbert", "dilbfrt", "dilbgrt", "dilbhrt",
                        "dilbirt", "dilbjrt", "dilbkrt", "dilblrt",
                        "dilbmrt", "dilbnrt", "dilbort", "dilbprt",
                        "dilbqrt", "dilbrrt", "dilbsrt", "dilbtrt",
                        "dilburt", "dilbvrt", "dilbwrt", "dilbxrt",
                        "dilbyrt", "dilbzrt", "dilbeat", "dilbebt",
                        "dilbect", "dilbedt", "dilbeet", "dilbeft",
                        "dilbegt", "dilbeht", "dilbeit", "dilbejt",
                        "dilbekt", "dilbelt", "dilbemt", "dilbent",
                        "dilbeot", "dilbept", "dilbeqt", "dilbert",
                        "dilbest", "dilbett", "dilbeut", "dilbevt",
                        "dilbewt", "dilbext", "dilbeyt", "dilbezt",
                        "dilbera", "dilberb", "dilberc", "dilberd",
                        "dilbere", "dilberf", "dilberg", "dilberh",
                        "dilberi", "dilberj", "dilberk", "dilberl",
                        "dilberm", "dilbern", "dilbero", "dilberp",
                        "dilberq", "dilberr", "dilbers", "dilbert",
                        "dilberu", "dilberv", "dilberw", "dilberx",
                        "dilbery", "dilberz"),
                replaces(dilbertWordSplits)
        )
    }
}