package video.report.mediaplayer.rxbus

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*


class RxBus private constructor() {
    private val mSubject: Subject<Any> = PublishSubject.create<Any>().toSerialized()
    private var mSubscriptionMap: HashMap<String, CompositeDisposable>? = null

    fun post(o: Any) {
        mSubject.onNext(o)
    }

    fun <T> toObservable(type: Class<T>): Flowable<T> {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type)
    }

    fun hasObservers(): Boolean {
        return mSubject.hasObservers()
    }

    fun <T> doSubscribe(type: Class<T>, next: Consumer<T>, error: Consumer<Throwable>): Disposable {
        return toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error)
    }

    fun addSubscription(o: Any, subscription: Disposable) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = HashMap()
        }
        val key = o.javaClass.name
        if (mSubscriptionMap!![key] != null) {
            mSubscriptionMap!![key]!!.add(subscription)
        } else {
            val compositeSubscription = CompositeDisposable()
            compositeSubscription.add(subscription)
            mSubscriptionMap!![key] = compositeSubscription
        }
    }

    fun unSubscribe(o: Any) {
        if (mSubscriptionMap == null) {
            return
        }

        val key = o.javaClass.name
        if (!mSubscriptionMap!!.containsKey(key)) {
            return
        }
        if (mSubscriptionMap!![key] != null) {
            mSubscriptionMap!![key]!!.dispose()
        }

        mSubscriptionMap!!.remove(key)
    }

    companion object {
        var instance: RxBus? = null
            get() {
                if (field == null) {
                    synchronized(RxBus::class.java) {
                        if (field == null) {
                            field = RxBus()
                        }
                    }
                }
                return field
            }
    }
}
