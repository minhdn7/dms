package vn.com.vsc.ptpm.VNPT_DMS.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import vn.com.vsc.ptpm.VNPT_DMS.R;
import vn.com.vsc.ptpm.VNPT_DMS.common.Config;
import vn.com.vsc.ptpm.VNPT_DMS.control.Controller;
import vn.com.vsc.ptpm.VNPT_DMS.control.ConvertFont;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.DonViSP;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.SP_SL;

public class DS_SPAdapter extends ArrayAdapter<SP_SL> {
    private Context context;
    private List<SP_SL> items;
    private ConvertFont conv = new ConvertFont();
    private Controller control;
    private int layoutResourceId;
    int pos_dvi = 0;
    double tong = 0;
    double tongTienTruocVAT = 0;
    double gia = 0;
    private OnCustomClickListener callback;

    public DS_SPAdapter(Context context, int layoutResourceId,
                        List<SP_SL> items, OnCustomClickListener callback) {
        super(context, layoutResourceId, items);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.items = items;
        this.callback = callback;
        this.control = new Controller(context);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolderItem holder = null;
        // if (row == null) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(layoutResourceId, parent, false);
        holder = new ViewHolderItem();
        holder.sp_sl = items.get(position);
        holder.btn_sua = (ImageButton) row.findViewById(R.id.btn_suaSP);
        holder.btn_sua.setTag(holder.sp_sl);
        holder.btn_sua.setOnClickListener(new CustomOnClickListener(callback,
                position));
        holder.btn_xoa = (ImageButton) row.findViewById(R.id.btn_xoaSP);
        holder.btn_xoa.setTag(holder.sp_sl);
        holder.btn_xoa.setOnClickListener(new CustomOnClickListener(callback,
                position));
        holder.numb = (TextView) row.findViewById(R.id.numb);
        holder.txt_item1 = (TextView) row.findViewById(R.id.txt_item1);
        holder.txt_item2 = (TextView) row.findViewById(R.id.txt_item2);
        holder.txt_item3 = (Spinner) row.findViewById(R.id.txt_item3);
        holder.txt_item4 = (TextView) row.findViewById(R.id.txt_item4);
        holder.txt_item5 = (TextView) row.findViewById(R.id.txt_item5);
        holder.txt_item6 = (EditText) row.findViewById(R.id.txt_item6);
        // } else {
        // holder = (ViewHolderItem) convertView.getTag();
        // }
        // gia = Double.parseDouble(holder.sp_sl.getSp().getGiaban());
        // dt.setValues(String.valueOf(gia) + " - 1");
        // Log.i("gia", String.valueOf(gia));
        holder.numb.setText(String.valueOf(position + 1));
        setItemSpin(holder);
        setSoLuongChangeListener(holder);

        setupItem(holder);
        row.setTag(holder);
        return row;
    }

    private List<DonViSP> getArr_dvi(String input) {
        List<DonViSP> arr_dvi = new ArrayList<DonViSP>();
        String s = conv.getUTF8StringFromNCR(input);
        String[] result = s.split("[;|]");
        int numbRlts = (result.length) / 5;
        for (int i = 0; i < numbRlts; i++) {
            DonViSP dvi = new DonViSP();
            for (int j = 1; j <= 5; j++) {
                dvi = new DonViSP(result[i * j], result[i * j + 1], result[i
                        * j + 2], result[i * j + 3], result[i * j + 4]);
            }
            arr_dvi.add(dvi);
        }
        return arr_dvi;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SP_SL getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolderItem {
        public int pos_spin = 0;
        public SP_SL sp_sl;
        public TextView numb, txt_item1, txt_item2, txt_item4, txt_item5;
        public Spinner txt_item3;
        public EditText txt_item6;
        public ImageButton btn_sua, btn_xoa;
        // public LinearLayout item_list_dsdh;
    }

    private void setupItem(final ViewHolderItem holder) {
        String txt1 = holder.sp_sl.getSp().getProduct_cat_id();
        if (txt1.equals("") || txt1.equals(null))
            holder.txt_item1.setText(holder.sp_sl.getSp().getProduct_cat_id());
        holder.txt_item2.setText(conv.getUTF8StringFromNCR(holder.sp_sl.getSp()
                .getName()));
        holder.txt_item6.setText(holder.sp_sl.getSl_dat());
        holder.txt_item3.setSelection(Integer.parseInt(holder.sp_sl
                .getSelected()));
        holder.txt_item5.setText((holder.sp_sl.getTon_kho()));
        holder.txt_item4.setText(NumbFormatF(holder.sp_sl.getDvi().getGia1()));
    }

    private void setItemSpin(final ViewHolderItem holder) {

        final List<DonViSP> arr_dvi = getArr_dvi(holder.sp_sl.getSp()
                .getDs_donvi());
        // holder.sp_sl.setDon_vi("0");
        if (arr_dvi.size() > 0) {
            // add san pham don vi vao vi tri 0
            String donViMacDinh = conv.getUTF8StringFromNCR(holder.sp_sl.getSp().getUnitDisplay());
            for(DonViSP item : arr_dvi){
                if(item.getTen_dvi().equals(donViMacDinh)){
                    arr_dvi.add(0, item);
                    break;
                }
            }
            // end
            // remove donvi bi trung (so sanh voi vi tri 0)
            for(int i = 1; i < arr_dvi.size(); i ++){
                if(arr_dvi.get(0).getTen_dvi().equals(arr_dvi.get(i).getTen_dvi())){
                    arr_dvi.remove(i);
                    break;
                }
            }
            // end

            SpinAdapDV sp_adap = new SpinAdapDV(context, R.layout.spinner_row,
                    arr_dvi);
            holder.txt_item3.setAdapter(sp_adap);

            holder.txt_item3
                    .setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int pos, long arg3) {
                            holder.pos_spin = pos;
                            holder.sp_sl.setSelected(String.valueOf(pos));
                            Log.i("pos_spin1", String.valueOf(holder.pos_spin));

                            String gia_tien = String.valueOf((Float
                                    .parseFloat(arr_dvi.get(pos).getGia1())));
                            gia = Float.parseFloat(arr_dvi.get(pos).getGia1());
                            holder.txt_item4.setText(NumbFormatF(gia_tien));
                            holder.sp_sl.getDvi().setGia1(gia_tien);
                            // ton kho = soluong (SanPham) /sluong(DonVi)
                            /************************************
                            TODO: công thức tính cũ hiển thị tồn kho
                             double tonkho = Double.parseDouble(holder.sp_sl
                             .getSp().getSoluong().trim())
                             / Double.parseDouble(arr_dvi.get(pos)
                             .getSluong());
                             String ton_kho = String.valueOf((tonkho));
                             holder.txt_item5.setText(NumbFormatF(ton_kho));
                             holder.sp_sl.setTon_kho((ton_kho));
                             ***************************************/

                            // end

                            String id = arr_dvi.get(pos).getId();
                            String ten_dvi = arr_dvi.get(pos).getTen_dvi();
                            String sluong = arr_dvi.get(pos).getSluong();
                            String gia1 = gia_tien;
                            String gia2 = arr_dvi.get(pos).getGia2();

                            DonViSP new_dvi = new DonViSP(id, sluong, ten_dvi,
                                    gia1, gia2);
                            holder.sp_sl.setDvi(new_dvi);
                            holder.txt_item1.setText(holder.sp_sl.getDvi()
                                    .getId());
                            // try {
                            tong = Double.parseDouble(holder.sp_sl.getSl_dat()
                                    .toString().trim())
                                    * Double.parseDouble(holder.sp_sl.getDvi()
                                    .getGia1().toString().trim());

                            if(holder.sp_sl.getSp().getTaxRate() != null){
                                tongTienTruocVAT = (Double.parseDouble(holder.sp_sl.getSl_dat()
                                        .toString().trim())
                                        * Double.parseDouble(holder.sp_sl.getDvi()
                                        .getGia1().toString().trim())) / (1 + Double.parseDouble(holder.sp_sl.getSp().getTaxRate()));
                                holder.sp_sl.setTongTienTruocVAT(String.valueOf(tongTienTruocVAT));
                            }
                            // } catch (Exception e) {
                            // tong = 0;
                            // }
                            holder.sp_sl.setTong_t_hang(String.valueOf(tong));

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
        }

    }

    private void setSoLuongChangeListener(final ViewHolderItem holder) {

        holder.txt_item6.addTextChangedListener(new TextWatcher() {
            private int mPosition;
            private boolean mActive;

            void setPosition(int position) {
                mPosition = position;
            }

            void setActive(boolean active) {
                mActive = active;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("") && control.isNumber(s.toString())) {
                    Config.isEmptyEdittext = false;
                    holder.sp_sl.setSl_dat(s.toString());
                    tong = Double.parseDouble(holder.sp_sl.getSl_dat()
                            .toString().trim())
                            * Double.parseDouble(holder.sp_sl.getDvi()
                            .getGia1().toString().trim());
                    holder.sp_sl.setTong_t_hang(String.valueOf(tong));

                    if(holder.sp_sl.getSp().getTaxRate() != null){
                        tongTienTruocVAT = (Double.parseDouble(holder.sp_sl.getSl_dat()
                                .toString().trim())
                                * Double.parseDouble(holder.sp_sl.getDvi()
                                .getGia1().toString().trim())) / (1 + Double.parseDouble(holder.sp_sl.getSp().getTaxRate()));
                        holder.sp_sl.setTongTienTruocVAT(String.valueOf(tongTienTruocVAT));
                    }
                }
                if (s.toString().equals("")) {
                    Config.isEmptyEdittext = true;
//                    Toast.makeText(getContext(), "Số lượng đặt hàng không được để trống", Toast.LENGTH_SHORT).show();
//                    holder.sp_sl.setSl_dat("");
//                    holder.sp_sl.setTong_t_hang(holder.sp_sl.getDvi() + "");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String NumbFormatF(String numb) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(Double.parseDouble(numb.toString().trim()));
    }

    public String NumbFormatI(String numb) {
        DecimalFormat df = new DecimalFormat();
        return df.format(Integer.parseInt(numb.toString().trim()));
    }

    public List<SP_SL> getItems() {
        return items;
    }

    public void setItems(List<SP_SL> items) {
        this.items = items;
    }

}