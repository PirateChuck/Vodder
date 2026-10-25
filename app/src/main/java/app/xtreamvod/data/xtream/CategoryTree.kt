package app.xtreamvod.data.xtream

/**
 * Xtream category lists are often a forest: folders like "DE Netflix" have child categories
 * linked via [CategoryDto.parentId], not series rows from `get_series`.
 */
object CategoryTree {
    fun usesHierarchy(all: List<CategoryDto>): Boolean =
        all.any { it.parentId != 0 }

    fun categoriesForParent(all: List<CategoryDto>, parentId: String): List<CategoryDto> {
        val parentKey = parentId.trim().toIntOrNull() ?: 0
        if (!usesHierarchy(all)) {
            return if (parentKey == 0) all else emptyList()
        }
        return all.filter { it.parentId == parentKey }
    }

    fun hasChildCategories(all: List<CategoryDto>, categoryId: String): Boolean {
        val parentKey = categoryId.trim().toIntOrNull() ?: return false
        return all.any { it.parentId == parentKey }
    }

    fun isFolder(all: List<CategoryDto>, category: CategoryDto): Boolean =
        hasChildCategories(all, category.categoryId)
}
