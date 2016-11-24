package com.ifnoif.launcher;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifnoif.launcher.util.LongArrayMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by syh on 2016/11/15.
 */
public class FolderAll extends LinearLayout {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_COMMON = 1;

    public static int COLUMN_COUNT = 4;

    private Launcher mLauncher;
    private List<CustomFolderInfo> mFolderList = new ArrayList<CustomFolderInfo>();
    private RecyclerView mRecycleView;

    private int mCellWidth =0;
    private int mCellHeight =0;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> myViewHolderAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == TYPE_TITLE) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.folder_item_title_layout, parent, false);
                return new TitleViewHolder(view);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.folder_item_layout, parent, false);
                return new ContentViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TitleViewHolder) {
                ((TitleViewHolder) holder).onBindView(getItem(position), position);
            } else if (holder instanceof ContentViewHolder) {
                ((ContentViewHolder) holder).onBindView(getItem(position), position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            CustomFolderInfo itemInfo = getItem(position);
            if (itemInfo.folderInfo != null) {
                return TYPE_TITLE;
            }
            return TYPE_COMMON;
        }

        public CustomFolderInfo getItem(int position) {
            return mFolderList.get(position);
        }


        @Override
        public int getItemCount() {
            return mFolderList.size();
        }
    };

    public FolderAll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    static FolderAll fromXML(Launcher launcher) {
        return (FolderAll) launcher.getLayoutInflater().inflate(R.layout.user_folder_all, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLauncher = (Launcher) getContext();
        DeviceProfile grid = mLauncher.getDeviceProfile();
        COLUMN_COUNT = (int) grid.inv.numHotseatIcons;
        mCellWidth = grid.hotseatCellWidthPx;
        mCellHeight = grid.hotseatCellHeightPx;

        mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(myViewHolderAdapter);
    }

    public void setFolderList(LongArrayMap<FolderInfo> copyBgFolders) {
        onOriginalDataSizeChanged(copyBgFolders);
    }

    public List<ItemInfo> getItemList(long folderId) {

        return null;
    }

    public void onOriginalDataSizeChanged(LongArrayMap<FolderInfo> copyBgFolders) {
        if (copyBgFolders == null) {
            return;
        }
        mFolderList.clear();
        Iterator<FolderInfo> iterator = copyBgFolders.iterator();
        while (iterator.hasNext()) {
            FolderInfo folderInfo = iterator.next();

            mFolderList.add(new CustomFolderInfo(folderInfo.id, folderInfo, null));
            int childSize = folderInfo.contents == null ? 0 : folderInfo.contents.size();
            if (childSize > 0) {
                int row = childSize / COLUMN_COUNT;
                int lastRowCount = (childSize % COLUMN_COUNT);
                for (int i = 0; i < row; i++) {
                    List<ShortcutInfo> list = new ArrayList<>();
                    CustomFolderInfo displayFolderInfo = new CustomFolderInfo(folderInfo.id, null, list);
                    for (int j = 0; j < COLUMN_COUNT; j++) {
                        list.add(folderInfo.contents.get(i * COLUMN_COUNT + j));
                    }
                    mFolderList.add(displayFolderInfo);
                }
                if (lastRowCount > 0) {
                    List<ShortcutInfo> list = new ArrayList<>();
                    CustomFolderInfo displayFolderInfo = new CustomFolderInfo(folderInfo.id, null, list);
                    for (int j = 0; j < lastRowCount; j++) {
                        list.add(folderInfo.contents.get(row * COLUMN_COUNT + j));
                    }
                    mFolderList.add(displayFolderInfo);
                }
            }
        }

        myViewHolderAdapter.notifyDataSetChanged();
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {

        public TitleViewHolder(View itemView) {
            super(itemView);
        }

        public void onBindView(CustomFolderInfo customFolderInfo, int position) {
            TextView title = (TextView) itemView.findViewById(R.id.title);
            title.setText("文件夹："+customFolderInfo.folderInfo.title);
        }
    }

    public static class CustomFolderInfo {
        public long folderId;
        public FolderInfo folderInfo;
        public List<ShortcutInfo> itemList = null;

        public CustomFolderInfo(long folderId, FolderInfo folderInfo, List<ShortcutInfo> list) {
            folderId = folderId;
            this.folderInfo = folderInfo;
            this.itemList = list;
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        private CellLayout cellLayout;

        public ContentViewHolder(View itemView) {
            super(itemView);
            cellLayout = new CellLayout(getContext());
            cellLayout.setFixedSize(getWidth(), mCellHeight);
            cellLayout.setCellDimensions(mCellWidth, mCellHeight);
            cellLayout.setGridSize(COLUMN_COUNT, 1);
            ((ViewGroup)itemView).addView(cellLayout,-1,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        }

        public void onBindView(CustomFolderInfo customFolderInfo, int position) {
            cellLayout.removeAllViewsInLayout();
            int i = 0;
            for (ShortcutInfo shortcutInfo : customFolderInfo.itemList) {
                BubbleTextView bubbleTextView = (BubbleTextView) mLauncher.createShortcut(cellLayout, shortcutInfo);

                ViewGroup.LayoutParams genericLp = bubbleTextView.getLayoutParams();
                CellLayout.LayoutParams lp;
                if (genericLp == null || !(genericLp instanceof CellLayout.LayoutParams)) {
                    lp = new CellLayout.LayoutParams(i, 0, 1, 1);
                } else {
                    lp = (CellLayout.LayoutParams) genericLp;
                    lp.cellX = i;
                    lp.cellY = 0;
                    lp.cellHSpan = 1;
                    lp.cellVSpan = 1;
                }

                cellLayout.addViewToCellLayout(bubbleTextView, i, (int) shortcutInfo.id, lp, true);
                i++;
            }

        }
    }

}
