package eu.kanade.tachiyomi.extension.all.mangaplus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaPlusResponse(
    val success: SuccessResult? = null,
    val error: ErrorResult? = null
)

@Serializable
data class ErrorResult(
    val englishPopup: Popup,
    val popups: List<Popup> = emptyList()
) {

    fun langPopup(lang: Language): Popup =
        popups.firstOrNull { it.language == lang } ?: englishPopup
}

@Serializable
data class Popup(
    val subject: String,
    val body: String,
    val language: Language? = Language.ENGLISH
)

@Serializable
data class SuccessResult(
    val isFeaturedUpdated: Boolean? = false,
    val titleRankingView: TitleRankingView? = null,
    val titleDetailView: TitleDetailView? = null,
    val mangaViewer: MangaViewer? = null,
    val allTitlesViewV2: AllTitlesViewV2? = null,
    val webHomeViewV3: WebHomeViewV3? = null
)

@Serializable
data class TitleRankingView(val titles: List<Title> = emptyList())

@Serializable
data class AllTitlesViewV2(
    @SerialName("AllTitlesGroup") val allTitlesGroup: List<AllTitlesGroup> = emptyList()
)

@Serializable
data class AllTitlesGroup(
    val theTitle: String,
    val titles: List<Title> = emptyList()
)

@Serializable
data class WebHomeViewV3(val groups: List<UpdatedTitleV2Group> = emptyList())

@Serializable
data class TitleDetailView(
    val title: Title,
    val titleImageUrl: String,
    val overview: String,
    val backgroundImageUrl: String,
    val nextTimeStamp: Int = 0,
    val viewingPeriodDescription: String = "",
    val nonAppearanceInfo: String = "",
    val firstChapterList: List<Chapter> = emptyList(),
    val lastChapterList: List<Chapter> = emptyList(),
    val isSimulReleased: Boolean = false,
    val chaptersDescending: Boolean = true
) {
    private val isWebtoon: Boolean
        get() = firstChapterList.all(Chapter::isVerticalOnly) &&
            lastChapterList.all(Chapter::isVerticalOnly)

    private val isOneShot: Boolean
        get() = chapterCount == 1 && firstChapterList.firstOrNull()
            ?.name?.equals("one-shot", true) == true

    private val chapterCount: Int
        get() = firstChapterList.size + lastChapterList.size

    private val isReEdition: Boolean
        get() = viewingPeriodDescription.contains(MangaPlus.REEDITION_REGEX)

    val isCompleted: Boolean
        get() = nonAppearanceInfo.contains(MangaPlus.COMPLETED_REGEX) || isOneShot

    val genres: List<String>
        get() = listOf(
            if (isSimulReleased && !isReEdition) "Simulrelease" else "",
            if (isOneShot) "One-shot" else "",
            if (isReEdition) "Re-edition" else "",
            if (isWebtoon) "Webtoon" else ""
        )
}

@Serializable
data class MangaViewer(
    val pages: List<MangaPlusPage> = emptyList(),
    val titleId: Int? = null,
    val titleName: String? = null
)

@Serializable
data class Title(
    val titleId: Int,
    val name: String,
    val author: String,
    val portraitImageUrl: String,
    val landscapeImageUrl: String,
    val viewCount: Int = 0,
    val language: Language? = Language.ENGLISH
)

enum class Language {
    ENGLISH,
    SPANISH,
    FRENCH,
    INDONESIAN,
    PORTUGUESE_BR,
    RUSSIAN,
    THAI
}

@Serializable
data class UpdatedTitleV2Group(
    val groupName: String,
    val titleGroups: List<OriginalTitleGroup> = emptyList()
)

@Serializable
data class OriginalTitleGroup(
    val theTitle: String,
    val titles: List<UpdatedTitle> = emptyList()
)

@Serializable
data class UpdatedTitle(val title: Title)

@Serializable
data class Chapter(
    val titleId: Int,
    val chapterId: Int,
    val name: String,
    val subTitle: String? = null,
    val startTimeStamp: Int,
    val endTimeStamp: Int,
    val isVerticalOnly: Boolean = false
)

@Serializable
data class MangaPlusPage(val mangaPage: MangaPage? = null)

@Serializable
data class MangaPage(
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val encryptionKey: String? = null
)
