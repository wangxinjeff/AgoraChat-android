package io.agora.chatdemo.group.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import io.agora.chat.CursorResult;
import io.agora.chat.Group;
import io.agora.chat.GroupInfo;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chatdemo.R;
import io.agora.chatdemo.chat.ChatActivity;
import io.agora.chatdemo.contact.SearchFragment;
import io.agora.chatdemo.general.callbacks.OnResourceParseCallback;
import io.agora.chatdemo.general.constant.DemoConstant;
import io.agora.chatdemo.global.BottomSheetChildHelper;
import io.agora.chatdemo.group.adapter.PublicGroupAdapter;
import io.agora.chatdemo.group.viewmodel.GroupContactViewModel;


public class PublicGroupFragment extends SearchFragment<GroupInfo> implements OnRefreshLoadMoreListener,
        OnItemClickListener, BottomSheetChildHelper {
    public RecyclerView rvList;
    private static final int PAGE_SIZE = 20;
    private String cursor;
    private GroupContactViewModel viewModel;
    private List<Group> allJoinGroups;
    protected List<GroupInfo> lastData=new ArrayList<>();


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rvList = findViewById(R.id.recycleview);
        rvList.setNestedScrollingEnabled(false);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlContactRefresh.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void searchText(String content) {
        if (TextUtils.isEmpty(content)) {
            mListAdapter.setData(lastData);
        } else {
            ArrayList<GroupInfo> groupInfos = new ArrayList<>(lastData);
            for (int i = 0; i < groupInfos.size(); i++) {
                if (!groupInfos.get(i).getGroupName().contains(content)) {
                    groupInfos.remove(i);
                    i--;
                }
            }
            mListAdapter.setData(groupInfos);
        }
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        viewModel.getPublicGroupObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<CursorResult<GroupInfo>>() {
                @Override
                public void onSuccess(CursorResult<GroupInfo> data) {
                    List<GroupInfo> groups = data.getData();
                    cursor = data.getCursor();
                    lastData.clear();
                    lastData.addAll(groups);
                    mListAdapter.setData(groups);
                }

                @Override
                public void onHideLoading() {
                    super.onHideLoading();
                    if (srlContactRefresh != null) {
                        srlContactRefresh.finishRefresh();
                    }
                }
            });
        });

        viewModel.getMorePublicGroupObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<CursorResult<GroupInfo>>() {
                @Override
                public void onSuccess(CursorResult<GroupInfo> data) {
                    cursor = data.getCursor();
                    List<GroupInfo> groups = data.getData();
                    lastData.addAll(groups);
                    mListAdapter.addData(groups);
                }

                @Override
                public void onHideLoading() {
                    super.onHideLoading();
                    if (srlContactRefresh != null) {
                        srlContactRefresh.finishLoadMore();
                    }
                }
            });
        });

        viewModel.getAllGroupsObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<Group>>() {
                @Override
                public void onSuccess(@Nullable List<Group> data) {
                    allJoinGroups = data;
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                }
            });
        });

    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getData();
    }

    @Override
    protected EaseBaseRecyclerViewAdapter initAdapter() {
        return new PublicGroupAdapter();
    }

    public void getData() {
        viewModel.getPublicGroups(PAGE_SIZE);
        viewModel.loadAllGroups();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (cursor != null) {
            viewModel.getMorePublicGroups(PAGE_SIZE, cursor);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getData();
    }

    @Override
    public void onItemClick(View view, int position) {
        if(allJoinGroups!=null&&allJoinGroups.contains(lastData.get(position))) {
            GroupInfo item = mListAdapter.getItem(position);
            ChatActivity.actionStart(mContext, item.getGroupId(), DemoConstant.CHATTYPE_GROUP);
        }else{
            PublicGroupDetailFragment publicGroupDetailFragment = new PublicGroupDetailFragment();
            Bundle bundle=new Bundle();
            bundle.putSerializable("group",lastData.get(position));
            publicGroupDetailFragment.setArguments(bundle);
            startFragment(publicGroupDetailFragment,null);
        }
    }

    @Override
    public boolean isShowTitlebarLeftLayout() {
        return true;
    }

    @Override
    public int getTitlebarTitle() {
        return R.string.group_public_group;
    }
}
