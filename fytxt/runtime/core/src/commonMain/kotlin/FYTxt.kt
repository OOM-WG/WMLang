@file:Suppress("PackageDirectoryMismatch")

package tf.gal.shirosu.fyl.fytxt

import cn.xz.tar.shirosu.fyl.fytxt.platformLocaleProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

@Suppress("unused")
interface FYTxtGroup {
	val name: String
	val stats: Map<out FYTxtTag, Double>
}

@Suppress("unused")
interface FYTxtTag {
	val name: String
	val pattern: Regex?
}

@Suppress("unused")
object FYTxtConfig {
	private val scope = CoroutineScope(SupervisorJob())

	private val _locTags = MutableStateFlow(platformLocaleProvider.getLocales())
	val locTags = _locTags.asStateFlow()

	private val _lock = MutableStateFlow(false)
	val lock = _lock.asStateFlow()

	private val _activeGroup = MutableStateFlow(null as FYTxtGroup?)
	val activeGroup = _activeGroup.asStateFlow()
	private lateinit var appTags: List<FYTxtTag>

	private val _activeTags = MutableStateFlow(emptyList<FYTxtTag>())
	val activeTags = _activeTags.asStateFlow()

	init {
		combine(_locTags, _activeGroup.filterNotNull().take(1)) { sysTags, _ ->
			if (!::appTags.isInitialized) emptyList() else filterTags(sysTags)
		}.distinctUntilChanged().onEach { _activeTags.value = it }.launchIn(scope)

		platformLocaleProvider.localeUpdates?.distinctUntilChanged()
			?.onEach { tags -> if (!_lock.value) _locTags.value = tags }?.launchIn(scope)
	}

	fun updateTags(tags: List<String>? = null, lock: Boolean? = null): List<String> {
		lock?.let { _lock.value = it }
		return (tags ?: if (!_lock.value) platformLocaleProvider.getLocales() else null)?.also {
			_locTags.value = it
			if (::appTags.isInitialized) _activeTags.value = filterTags(it)
		} ?: _locTags.value
	}

	fun updateGroup(group: FYTxtGroup) = group.also { _activeGroup.value = it }

	private fun filterTags(sysTags: List<String>) = LinkedHashSet<FYTxtTag>().apply {
		sysTags.forEach { sysTag ->
			addAll(appTags.filter { it.pattern?.matches(sysTag) ?: sysTag.startsWith(it.name) })
		}
	}.toList()

	/**
	 * @suppress used by yourself
	 */
	fun init(group: FYTxtGroup, tags: List<FYTxtTag>) {
		appTags = tags
		_activeGroup.value = group
		_activeTags.value = filterTags(_locTags.value)
	}
}