package com.jayvijay.gitpeek.presentation.navigation

import com.jayvijay.gitpeek.presentation.navigation.base.AppRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GitPeekNavigatorTest {
    private lateinit var navigator: GitPeekNavigator

    private val startRoute = TestRoute("start")
    private val routeA = TestRoute("A")
    private val routeB = TestRoute("B")
    private val routeC = TestRoute("C")

    @Before
    fun setup() {
        navigator = GitPeekNavigator(startRoute)
    }

    @Test
    fun `should have start destination as only item in backStack`() {
        assertEquals(1, navigator.backStack.size)
        assertEquals(startRoute, navigator.backStack.first())
    }

    @Test
    fun `should add route to end of backStack`() {
        navigator.navigateTo(routeA)

        assertEquals(2, navigator.backStack.size)
        assertEquals(routeA, navigator.backStack.last())
    }

    @Test
    fun `should add multiple routes in order`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)
        navigator.navigateTo(routeC)

        assertEquals(4, navigator.backStack.size)
        assertEquals(listOf(startRoute, routeA, routeB, routeC), navigator.backStack)
    }

    @Test
    fun `should allow duplicate routes`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeA)

        assertEquals(3, navigator.backStack.size)
        assertEquals(routeA, navigator.backStack[1])
        assertEquals(routeA, navigator.backStack[2])
    }

    @Test
    fun `should return false when backStack has only start destination`() {
        val result = navigator.navigateBack()

        assertFalse(result)
        assertEquals(1, navigator.backStack.size)
    }

    @Test
    fun `should return true and remove last route when backStack has multiple items`() {
        navigator.navigateTo(routeA)

        val result = navigator.navigateBack()

        assertTrue(result)
        assertEquals(1, navigator.backStack.size)
        assertEquals(startRoute, navigator.backStack.last())
    }

    @Test
    fun `should navigate back through multiple routes`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)

        navigator.navigateBack()
        assertEquals(routeA, navigator.backStack.last())

        navigator.navigateBack()
        assertEquals(startRoute, navigator.backStack.last())

        assertFalse(navigator.navigateBack())
    }

    @Test
    fun `should remove routes from matching route inclusive`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)
        navigator.navigateTo(routeC)

        navigator.popUpTo(inclusive = true) { it == routeB }

        assertEquals(listOf(startRoute, routeA), navigator.backStack)
    }

    @Test
    fun `should do nothing when predicate matches no route`() {
        navigator.navigateTo(routeA)

        navigator.popUpTo(inclusive = true) { it == routeC }

        assertEquals(listOf(startRoute, routeA), navigator.backStack)
    }

    @Test
    fun `should clear entire stack when popping start route inclusive`() {
        navigator.navigateTo(routeA)

        navigator.popUpTo(inclusive = true) { it == startRoute }

        assertTrue(navigator.backStack.isEmpty())
    }

    @Test
    fun `should remove routes after matching route when not inclusive`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)
        navigator.navigateTo(routeC)

        navigator.popUpTo(inclusive = false) { it == routeB }

        assertEquals(listOf(startRoute, routeA, routeB), navigator.backStack)
    }

    @Test
    fun `should keep all routes when not inclusive and match is last`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)

        navigator.popUpTo(inclusive = false) { it == routeB }

        assertEquals(listOf(startRoute, routeA, routeB), navigator.backStack)
    }

    @Test
    fun `should pop to last matching route when duplicates exist`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeC)

        navigator.popUpTo(inclusive = false) { it == routeA }

        assertEquals(listOf(startRoute, routeA, routeB, routeA), navigator.backStack)
    }

    @Test
    fun `should clear backStack and add new route`() {
        navigator.navigateTo(routeA)
        navigator.navigateTo(routeB)

        navigator.resetTo(routeC)

        assertEquals(1, navigator.backStack.size)
        assertEquals(routeC, navigator.backStack.first())
    }

    @Test
    fun `should work on fresh navigator`() {
        navigator.resetTo(routeA)

        assertEquals(1, navigator.backStack.size)
        assertEquals(routeA, navigator.backStack.first())
    }

    @Test
    fun `should replace last route with new route`() {
        navigator.navigateTo(routeA)

        navigator.replaceCurrent(routeB)

        assertEquals(2, navigator.backStack.size)
        assertEquals(startRoute, navigator.backStack.first())
        assertEquals(routeB, navigator.backStack.last())
    }

    @Test
    fun `should replace start destination when only one route`() {
        navigator.replaceCurrent(routeA)

        assertEquals(1, navigator.backStack.size)
        assertEquals(routeA, navigator.backStack.first())
    }

    @Test
    fun `should add route when backStack is empty after popUpTo`() {
        navigator.popUpTo(inclusive = true) { it == startRoute }

        navigator.replaceCurrent(routeA)

        assertEquals(1, navigator.backStack.size)
        assertEquals(routeA, navigator.backStack.first())
    }

    private data class TestRoute(
        val id: String,
    ) : AppRoute
}
