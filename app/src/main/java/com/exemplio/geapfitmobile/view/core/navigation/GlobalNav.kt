import androidx.navigation.NavHostController
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens
import java.lang.ref.WeakReference

object GlobalNav {
    private var rootRef: WeakReference<NavHostController>? = null
    private var bottomRef: WeakReference<NavHostController>? = null

    fun setRoot(controller: NavHostController) { rootRef = WeakReference(controller) }
    fun setBottom(controller: NavHostController) { bottomRef = WeakReference(controller) }

    val root: NavHostController? get() = rootRef?.get()
    val bottom: NavHostController? get() = bottomRef?.get()

    fun navigateRoot(route: TabScreens): Boolean =
        root?.let { it.navigate(route); true } ?: false

    fun navigateBottom(route: TabScreens): Boolean =
        bottom?.let { it.navigate(route); true } ?: false

    fun navigateUpRoot(): Boolean = root?.navigateUp() ?: false
    fun navigateUpBottom(): Boolean = bottom?.navigateUp() ?: false

    fun popToRoot(route: TabScreens, inclusive: Boolean = false): Boolean =
        root?.popBackStack(route, inclusive) ?: false

    fun popToBottom(route: TabScreens, inclusive: Boolean = false): Boolean =
        bottom?.popBackStack(route, inclusive) ?: false
}