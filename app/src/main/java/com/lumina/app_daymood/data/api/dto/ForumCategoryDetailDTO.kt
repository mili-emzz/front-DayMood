package com.lumina.app_daymood.data.api.dto

import com.google.gson.annotations.SerializedName

data class ForumCategoryDetailDTO(
    @SerializedName("id") val id: String,
    @SerializedName("min_age") val min_age: Int,
    @SerializedName("max_age") val max_age: Int,
    @SerializedName("id_category") val id_category: Int,
    // La API también retorna "_count", pero podemos mapearlo como un entero o ignorarlo si no se requiere.
    // También retorna posts si ya hay creados, u omitimos esta lista dependiendo de si es solo detail.
    @SerializedName("posts") val posts: List<PostDTO>? = emptyList()
)

data class ForumCategoryResponse(
    // La API directamente devuelve un array de objetos. 
    // Usaremos List<ForumCategoryDetailDTO> de retorno en el ApiService en lugar del objeto Response genérico si la API no envuelve en { "data": [] }
)
