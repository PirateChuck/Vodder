package app.xtreamvod

import app.xtreamvod.data.xtream.CategoryDto
import app.xtreamvod.data.xtream.CategoryTree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryTreeTest {
    private val flat = listOf(
        CategoryDto("1", "Action", parentId = 0),
        CategoryDto("2", "Comedy", parentId = 0),
    )

    private val nested = listOf(
        CategoryDto("10", "DE Netflix", parentId = 0),
        CategoryDto("11", "DE Amazon", parentId = 0),
        CategoryDto("101", "Drama", parentId = 10),
        CategoryDto("102", "Comedy", parentId = 10),
    )

    @Test
    fun flatPanel_showsAllAtRoot() {
        assertFalse(CategoryTree.usesHierarchy(flat))
        assertEquals(2, CategoryTree.categoriesForParent(flat, "0").size)
    }

    @Test
    fun nestedPanel_showsOnlyRootFoldersAtTop() {
        assertTrue(CategoryTree.usesHierarchy(nested))
        assertEquals(2, CategoryTree.categoriesForParent(nested, "0").size)
        assertEquals("DE Netflix", CategoryTree.categoriesForParent(nested, "0")[0].categoryName)
    }

    @Test
    fun nestedPanel_opensChildrenForFolder() {
        val children = CategoryTree.categoriesForParent(nested, "10")
        assertEquals(2, children.size)
        assertTrue(CategoryTree.isFolder(nested, CategoryDto("10", "DE Netflix")))
        assertFalse(CategoryTree.isFolder(nested, CategoryDto("101", "Drama")))
    }
}
