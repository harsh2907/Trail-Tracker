package com.example.trailtracker.utils

sealed class RequestState<out T> {
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    /**
     * Returns data from a [Success].
     * @throws ClassCastException If the current state is not [Success]
     *  */
    fun getSuccessData() = (this as Success).data

    fun getSuccessDataOrNull(): T? = (this as? Success)?.data
    /**
     * Returns data from a [Error].
     * @throws ClassCastException If the current state is not [Error]
     *  */
    fun getErrorMessage() = (this as Error).message

    fun getErrorMessageOrNull(): String? = (this as? Error)?.message

//Do not remove this
/*    @Composable
    fun DisplayResult(
        transitionAnimation: ContentTransform = fadeIn(tween(durationMillis = 300)) togetherWith
                fadeOut(tween(durationMillis = 300)),
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable (T) -> Unit,
        onError: @Composable (String) -> Unit,
    ) {
        AnimatedContent(
            targetState = this,
            transitionSpec = {
                transitionAnimation
            },
            label = "Content Animation"
        ) { state ->
            when (state) {
                is Idle -> {
                    onIdle?.invoke()
                }

                is Loading -> {
                    onLoading()
                }

                is Success -> {
                    onSuccess(state.getSuccessData())
                }

                is Error -> {
                    onError(state.getErrorMessage())
                }
            }
        }
    }*/
}