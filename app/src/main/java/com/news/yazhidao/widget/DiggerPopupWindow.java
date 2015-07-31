package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.pages.LengjingFgt;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.customdialog.Effectstype;
import com.news.yazhidao.widget.customdialog.SuperDialogBuilder;

import java.util.ArrayList;


/**
 * Created by ariesy on 2015/7/16.
 */
public class DiggerPopupWindow extends PopupWindow implements View.OnClickListener {

    private Activity m_pContext;
    private LengjingFgt mLengJingFgt;
    private View mMenuView;
    private int itemCount;
    private AlbumAdapter adapter;

    private TextView tv_cancel;
    private TextView tv_source_url;
    private TextView tv_confirm;
    private EditText et_content;
    private GridView gv_album;
    private HorizontalScrollView album_scollView;
    private LinearLayout ll_digger_source;
    private int position;
    private LinearLayout album_item_layout;
    private ArrayList<Album> albumList;
    private int viewcount = 0;
    private int width;
    private int height;
    private InputMethodManager imm;
    /**
     * 是否显示剪切中的数据
     */
    private boolean isShowClipboardContent = true;

    public DiggerPopupWindow(LengjingFgt lengjingFgt, Activity context, String itemCount, ArrayList<Album> list, int position, boolean isShowClipboardContent) {
        super(context);
        m_pContext = context;
        this.mLengJingFgt = lengjingFgt;
        this.position = position;
        this.isShowClipboardContent = isShowClipboardContent;
        if (albumList != null) {
            albumList.clear();
        }

        if (itemCount != null) {
            this.itemCount = Integer.parseInt(itemCount);
        }

        imm = (InputMethodManager) m_pContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        albumList = new ArrayList<Album>();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                albumList.add(list.get(i));
            }
        }

        width = GlobalParams.screenWidth;
        height = GlobalParams.screenHeight;

        findHeadPortraitImageViews();
        loadData();

        if (isShowClipboardContent) {
            showClipboardDialog(context);
        }
    }

    private void findHeadPortraitImageViews() {

        viewcount = 0;
        mMenuView = View.inflate(m_pContext, R.layout.popup_window_add_digger, null);
        mMenuView.setFocusableInTouchMode(true);
        mMenuView.setOnKeyListener(new View.OnKeyListener() {
            @Override

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    dismiss();
                    return true;

                }
                return false;
            }

        });

        ll_digger_source = (LinearLayout) mMenuView.findViewById(R.id.ll_digger_source);
        if (position == 2) {
            ll_digger_source.setVisibility(View.GONE);
        }

        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        tv_source_url = (TextView) mMenuView.findViewById(R.id.tv_source_url);
        tv_confirm = (TextView) mMenuView.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        et_content = (EditText) mMenuView.findViewById(R.id.et_content);
        album_scollView = (HorizontalScrollView) mMenuView.findViewById(R.id.album_scollView);
        album_item_layout = (LinearLayout) mMenuView.findViewById(R.id.album_item_layout);

        for (int i = 0; i < albumList.size(); i++) {
            RelativeLayout layout = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));

            layout.setLayoutParams(params);
            ImageView ivBgIcon = (ImageView) layout.findViewById(R.id.iv_bg_icon);
            LetterSpacingTextView tvName = (LetterSpacingTextView) layout.findViewById(R.id.tv_name);
            tvName.setTextSize(16);
            final ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
            final RelativeLayout rl_album = (RelativeLayout) layout.findViewById(R.id.rl_album);
            rl_album.setTag(viewcount);

            rl_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) rl_album.getTag();
                    Album album = albumList.get(tag);

                    iv_selected.setVisibility(View.VISIBLE);
                    album.setSelected(true);

                    for (int i = 0; i < albumList.size(); i++) {
                        if (i != tag) {
                            RelativeLayout layout = (RelativeLayout) album_item_layout.getChildAt(i);
                            ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
                            iv_selected.setVisibility(View.GONE);
                            albumList.get(i).setSelected(false);

                        }
                    }
                }
            });
            Album album = albumList.get(i);

            if (albumList != null && albumList.size() > 0) {
                tvName.setText(album.getAlbum());
            }

            if (album.isSelected()) {
                iv_selected.setVisibility(View.VISIBLE);
            } else {
                iv_selected.setVisibility(View.INVISIBLE);
            }

            album_item_layout.addView(layout);
            viewcount++;
        }


        RelativeLayout layout_add = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));//4635
        layout_add.setLayoutParams(params);

        final RelativeLayout rl_album = (RelativeLayout) layout_add.findViewById(R.id.rl_album);
        final RelativeLayout rl_add_album = (RelativeLayout) layout_add.findViewById(R.id.rl_add_album);
        rl_album.setVisibility(View.GONE);
        rl_add_album.setVisibility(View.VISIBLE);

        rl_add_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAlbumPopupWindow window = new AddAlbumPopupWindow(m_pContext, new AddAlbumPopupWindow.AddAlbumListener() {

                    @Override
                    public void add(Album album) {
                        if (album != null) {
                            //添加新专辑的时候,要默认新专辑为选中,所以要把老数据全部置为false
                            for (Album item : albumList) {
                                item.setSelected(false);
                            }
                            albumList.add(album);

                            RelativeLayout layout = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));

                            layout.setLayoutParams(params);
                            ImageView ivBgIcon = (ImageView) layout.findViewById(R.id.iv_bg_icon);
                            ivBgIcon.setBackgroundResource(Integer.parseInt(album.getId()));
                            LetterSpacingTextView tvName = (LetterSpacingTextView) layout.findViewById(R.id.tv_name);
                            tvName.setTextSize(16);
                            final ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
                            final RelativeLayout rl_album = (RelativeLayout) layout.findViewById(R.id.rl_album);
                            rl_album.setTag(viewcount);

                            for (int i = 0; i < albumList.size(); i++) {
                                if (i != viewcount) {
                                    RelativeLayout layout_temp = (RelativeLayout) album_item_layout.getChildAt(i);
                                    ImageView iv_selected_temp = (ImageView) layout_temp.findViewById(R.id.iv_selected);
                                    iv_selected_temp.setVisibility(View.GONE);
                                    albumList.get(i).setSelected(false);

                                }
                            }

                            rl_album.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int tag = (int) rl_album.getTag();
                                    Album album = albumList.get(tag);

                                    iv_selected.setVisibility(View.VISIBLE);
                                    album.setSelected(true);

                                    for (int i = 0; i < albumList.size(); i++) {
                                        if (i != tag) {
                                            albumList.get(i).setSelected(false);
                                            RelativeLayout layout = (RelativeLayout) album_item_layout.getChildAt(i);
                                            ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
                                            iv_selected.setVisibility(View.GONE);
                                            albumList.get(i).setSelected(false);
                                        }
                                    }
                                }
                            });

                            tvName.setText(album.getAlbum());

                            if (album.isSelected()) {
                                iv_selected.setVisibility(View.VISIBLE);
                            } else {
                                iv_selected.setVisibility(View.INVISIBLE);
                            }

                            imm.hideSoftInputFromWindow(mMenuView.getWindowToken(), 0);

                            album_item_layout.addView(layout, viewcount);
                            albumList.add(album);
                            viewcount++;
                        }
                    }
                });
                window.setFocusable(true);
                window.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });

        album_item_layout.addView(layout_add);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void loadData() {

    }

    public void setDigNewsTitleAndUrl(String title, String url) {
        et_content.setText(title);
        et_content.setSelection(title.length());
        tv_source_url.setText(url);
    }

    private void showClipboardDialog(final Context pContext) {
        ClipboardManager cbm = (ClipboardManager) pContext.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData primaryClip = cbm.getPrimaryClip();
        if (primaryClip.getItemCount()!=0){
            final SuperDialogBuilder _DialogBuilder = SuperDialogBuilder.getInstance(pContext);
            _DialogBuilder.withMessage("是否要使用剪切板中的数据进行挖掘?")
                    .withDuration(400)
                    .withIcon(R.drawable.app_icon_version2)
                    .withTitle("温馨提示")
                    .withEffect(Effectstype.Sidefill)
                    .withButton1Text("OK")
                    .withButton2Text("Cancel")
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _DialogBuilder.dismiss();
                            et_content.setText(primaryClip.getItemAt(0).getText());
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _DialogBuilder.dismiss();
                }
            }).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                String inputTitle = et_content.getText().toString();
                String inputUrl = tv_source_url.getText().toString();
                if (TextUtils.isEmpty(inputTitle)) {
                    ToastUtil.toastShort("亲,挖掘内容不能为空!");
                } else {
                    //判断用户选择的是哪一个专辑
                    int index = 0;
                    for (; index < albumList.size(); index++) {
                        if (albumList.get(index).isSelected()) {
                            break;
                        }
                    }
                    //通知外面的LengJingFgt 数据发生了变化
                    Logger.i("jigang", "input url = " + inputUrl);
                    mLengJingFgt.updateSpecialList(index, inputTitle, inputUrl, albumList.get(index));
                    this.dismiss();
                }
                break;
        }
    }

    class AlbumAdapter extends BaseAdapter {
        Context mContext;

        public AlbumAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return albumList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_album, null, false);
                holder.ivBgIcon = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
                holder.tvName = (LetterSpacingTextView) convertView.findViewById(R.id.tv_name);
                holder.tvName.setFontSpacing(5);
                holder.tvName.setTextSize(16);
                holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
                holder.rl_album = (RelativeLayout) convertView.findViewById(R.id.rl_album);
                holder.rl_add_album = (RelativeLayout) convertView.findViewById(R.id.rl_add_album);
                holder.iv_add_album = (ImageView) convertView.findViewById(R.id.iv_add_album);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.rl_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Album album = albumList.get(position);

                    boolean isSelected = album.isSelected();
                    if (isSelected) {
                        holder.iv_selected.setVisibility(View.INVISIBLE);
                        album.setSelected(false);
                    } else {
                        holder.iv_selected.setVisibility(View.VISIBLE);
                        album.setSelected(true);
                    }

                    for (int i = 0; i < albumList.size(); i++) {
                        if (i != position) {
                            albumList.get(i).setSelected(false);
                        }
                    }
                }
            });

            holder.rl_add_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAlbumPopupWindow window = new AddAlbumPopupWindow(m_pContext, new AddAlbumPopupWindow.AddAlbumListener() {

                        @Override
                        public void add(Album album) {
                            if (album != null) {
                                //添加新专辑的时候,要默认新专辑为选中,所以要把老数据全部置为false
                                for (Album item : albumList) {
                                    item.setSelected(false);
                                }
                                albumList.add(album);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    window.setFocusable(true);
                    window.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);
                }
            });


            if (position == albumList.size()) {

                holder.rl_album.setVisibility(View.GONE);
                holder.rl_add_album.setVisibility(View.VISIBLE);

            } else {

                holder.rl_album.setVisibility(View.VISIBLE);
                holder.rl_add_album.setVisibility(View.GONE);

                Album album = albumList.get(position);

                if (albumList != null && albumList.size() > 0) {
                    holder.tvName.setText(album.getAlbum());
                }

                if (album.isSelected()) {
                    holder.iv_selected.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_selected.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }
    }

    class Holder {
        ImageView ivBgIcon;
        LetterSpacingTextView tvName;
        ImageView iv_selected;
        RelativeLayout rl_album;
        RelativeLayout rl_add_album;
        ImageView iv_add_album;
    }
}
