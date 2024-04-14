package com.example.securechat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import com.bumptech.glide.Glide
import com.example.securechat.R
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.data.model.MessageGist
import com.example.securechat.databinding.ActivityChatBinding
import com.example.securechat.listeners.SwipeListener
import com.example.securechat.ui.chat.chatList.ChatListAdapter
import com.example.securechat.utils.AppConstants
import com.example.securechat.utils.CommonMethods
import com.example.securechat.utils.DialogHelper
import com.example.securechat.utils.UserInfo
import com.github.dhaval2404.imagepicker.ImagePicker

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatListAdapter

    fun open(context: Context, channelGist: ChannelGist) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(AppConstants.CHANNEL_GIST_NAME, channelGist)
        context.startActivity(intent)
    }

    private fun getIntentData(): ChannelGist {
        return intent?.getParcelableExtra(AppConstants.CHANNEL_GIST_NAME)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetwork()
    }

    private fun checkNetwork() {
        if (CommonMethods(this@ChatActivity).isConnected) {
            setUpUi()
        } else {
            DialogHelper.simpleDialog(
                this@ChatActivity, resources.getString(R.string.no_internet_msg)
            ) {
                checkNetwork()
            }
        }
    }

    private fun setUpUi() {
        val channelGist = getIntentData()
        setUpViewModel(channelGist)
        setUpToolbar(channelGist)
        setUpChatListRV()
        UserInfo(this@ChatActivity).userId?.let {
            viewModel.listenEvents(this@ChatActivity, it)
        }
        setUpAttach()
        setUpSendBtn()
        setUpBackBtn()
        setUpFetchChats()
        setUpReceiveNewMessage()
    }

    private fun setUpReceiveNewMessage() {
        viewModel.newMessage.observe(this@ChatActivity) {
            chatAdapter.addToFirst(it)
        }
    }

    private fun setUpBackBtn() {
        binding.chatToolbar.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        onBackPressedDispatcher.addCallback {
            finish()
        }
    }

    private fun setUpViewModel(channelGist: ChannelGist) {
        viewModel = ViewModelProvider(
            this@ChatActivity,
            ChatViewModel.provideViewModelFactory(::runOnUiThread, channelGist.channelId)
        )[ChatViewModel::class.java]
    }

    private fun setUpToolbar(channelGist: ChannelGist) {
        viewModel.channelGistData.observe(this@ChatActivity) {
            Glide.with(this@ChatActivity).load(it.displayPic).into(binding.chatToolbar.profilePic)
            binding.chatToolbar.personName.text = it.name
        }
        viewModel.updateChannelGistData(channelGist)
    }

    private fun setUpFetchChats() {
        viewModel.messages.observe(this@ChatActivity) {
            it?.let { messages ->
                chatAdapter.updateMessageList(messages)
                if (messages.isNotEmpty() && chatAdapter.itemCount <= 30) {
                    binding.chatListRv.scrollToPosition(0)
                }
            }
        }
        UserInfo(this@ChatActivity).userId?.let {
            viewModel.getAllMessages(null, it)
        }
    }

    private fun loadMoreData(lastMsgId: String) {
        UserInfo(this@ChatActivity).userId?.let {
            viewModel.getAllMessages(lastMsgId, it)
        }
    }


    private fun setUpChatListRV() {
        val layoutManager = LinearLayoutManager(
            this@ChatActivity, LinearLayoutManager.VERTICAL, true
        )
        chatAdapter = ChatListAdapter(this@ChatActivity)
        binding.chatListRv.adapter = chatAdapter
        binding.chatListRv.layoutManager = layoutManager
        binding.chatListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    SCROLL_STATE_SETTLING -> {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemCount = layoutManager.findFirstVisibleItemPosition()

                        if (visibleItemCount + firstVisibleItemCount + 5 >= totalItemCount && totalItemCount > 0) {
                            val lastItem = chatAdapter.getLastItem()
                            lastItem?.let {
                                loadMoreData(it.id)
                            }
                        }
                    }
                }
            }
        })
//        binding.chatListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//
//                when (newState) {
//                    SCROLL_STATE_SETTLING -> {
//                        val visibleItemCount = layoutManager.childCount
//                        val totalItemCount = layoutManager.itemCount
//                        val firstVisibleItemCount = layoutManager.findFirstVisibleItemPosition()
//
//                        if (visibleItemCount + firstVisibleItemCount + 5 >= totalItemCount && totalItemCount > 0) {
//                            val lastItem = chatAdapter.getLastItem()
//                            lastItem?.let {
//                                loadMoreData(it.id)
//                            }
//                        }
//                    }
//                }
//            }
//        })
    }

    private fun setUpSendBtn() {
        binding.sendButton.setOnClickListener {
            val text = binding.sendText.text.toString()
            if (text.isBlank()) {
                binding.sendText.error = resources.getString(R.string.empty_text_error)
            } else {
                viewModel.sendText(text)
                binding.sendText.setText("")
                Toast.makeText(this@ChatActivity, "Message has been sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpAttach() {
        binding.attachImage.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data!!

            showDialogAttachImageDialog(imageUri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialogAttachImageDialog(imageUri: Uri) {
        Glide.with(this@ChatActivity).load(imageUri).into(binding.attachImg)
        binding.attachImg.visibility = View.VISIBLE
        binding.attachImgCross.visibility = View.VISIBLE
        binding.attachImgCross.setOnClickListener {
            binding.attachImg.visibility = View.GONE
            binding.attachImgCross.visibility = View.GONE
        }
    }
}