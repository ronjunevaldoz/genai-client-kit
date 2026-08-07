package io.github.ronjunevaldoz.genaiclient.elevenlabs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ElevenLabsProjectDto(
    @SerialName("project_id") val projectId: String,
    val name: String,
    @SerialName("create_date_unix") val createDateUnix: Long? = null,
    @SerialName("default_model_id") val defaultModelId: String? = null,
)

@Serializable
internal data class ElevenLabsProjectsListDto(
    val projects: List<ElevenLabsProjectDto> = emptyList(),
)

@Serializable
internal data class ElevenLabsChapterDto(
    @SerialName("chapter_id") val chapterId: String,
    val name: String,
)

@Serializable
internal data class ElevenLabsChaptersListDto(
    val chapters: List<ElevenLabsChapterDto> = emptyList(),
)

@Serializable
internal data class ElevenLabsSnapshotDto(
    @SerialName("snapshot_id") val snapshotId: String,
    @SerialName("created_at_unix") val createdAtUnix: Long? = null,
)

@Serializable
internal data class ElevenLabsSnapshotsListDto(
    val snapshots: List<ElevenLabsSnapshotDto> = emptyList(),
)
