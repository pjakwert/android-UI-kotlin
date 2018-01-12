package com.example.pjakwert.uidemo

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.pjakwert.uidemo.model.Item
import com.example.pjakwert.uidemo.model.SearchResult
import com.example.pjakwert.uidemo.network.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.details_item.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class DetailsFragment: DialogFragment() {

    private var imageUrl : String? = null
    private var title : String? = null
    private var imdbId : String? = null

    lateinit var mCompositeDisposable : CompositeDisposable
    lateinit var mLoadingBar : ProgressBar

    val TAG = this::class.java.simpleName
    val BASE_URL = "http://www.omdbapi.com/"
    val API_KEY = "3110888f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = arguments.get("imageUrl") as String?
        title = arguments.get("title") as String?
        imdbId = arguments.get("imdbId") as String?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.details_item, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingBar = getView()!!.findViewById(R.id.loadingBar)

        titleView.text = title

        val picasso = Picasso.with(getView()!!.context)
        //picasso.setIndicatorsEnabled(true)

        picasso.load(imageUrl)
                .placeholder(R.drawable.empty_placeholder)
                .error(R.drawable.empty_placeholder)
                //.resize(270,400)
                .fit()
                .into(imageView)

        plotTextView.text = ""

        mCompositeDisposable = CompositeDisposable()
        loadJson()
    }

    override fun onDetach() {
        super.onDetach()
        mCompositeDisposable.clear()
    }

    private fun loadJson() {
        mLoadingBar.visibility = View.VISIBLE

        val requestDetailsInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DetailsRequestInterface::class.java)

        mCompositeDisposable.add(requestDetailsInterface
                .getData( API_KEY, imdbId!!) // page 1
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(item: Item) {
        mLoadingBar.visibility = View.GONE

        plotTextView.text = """Released: ${item.Released},
            |Director: ${item.Director}
            |Genre: ${item.Genre}
            |Type: ${item.Type}
            |Plot: ${item.Plot}""".trimMargin()

        Log.d(TAG, "adding Item: $item " )
    }


    private fun handleError(error: Throwable) {
        mLoadingBar.visibility = View.GONE
        Log.d(TAG, error.localizedMessage)
        showErrorMessage( error.localizedMessage )
    }


    fun showErrorMessage(text : String) {
        Snackbar.make(this.activity.window.decorView.rootView, text, Snackbar.LENGTH_SHORT).applyBackgroundColor(R.color.RED).show()
    }
}





fun makeDetailsDialogFragment( imageUrl : String, title : String, imdbId : String ) : DetailsFragment {

    var args = Bundle()
    args.putString("imageUrl", imageUrl)
    args.putString("title", title)
    args.putString("imdbId", imdbId)

    val fragment = DetailsFragment()

    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_ThemeOverlay_AppCompat_Dialog)

    fragment.arguments = args

    return fragment
}