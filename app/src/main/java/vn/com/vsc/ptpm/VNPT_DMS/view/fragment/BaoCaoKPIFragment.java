package vn.com.vsc.ptpm.VNPT_DMS.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vn.com.vsc.ptpm.VNPT_DMS.R;
import vn.com.vsc.ptpm.VNPT_DMS.adapter.Rpt_Kpi_Adapter;
import vn.com.vsc.ptpm.VNPT_DMS.common.mAF;
import vn.com.vsc.ptpm.VNPT_DMS.control.API_code;
import vn.com.vsc.ptpm.VNPT_DMS.control.Controller;
import vn.com.vsc.ptpm.VNPT_DMS.control.ConvertFont;
import vn.com.vsc.ptpm.VNPT_DMS.control.NorAndNop;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.BCTongHop;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.DonHang;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.DonHangTKC;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.NVKD;
import vn.com.vsc.ptpm.VNPT_DMS.model.model.Rpt_Kpi_Model;

public class BaoCaoKPIFragment extends ListFragment {

    public static final String TAG = BaoCaoKPIFragment.class.getSimpleName();

    Button btn_searchKPI;
    Spinner sp_NVKD;
    EditText et_startDateKPI, et_endDateKPI;
    ImageView iv_startDateKPI, iv_endDateKPI;
    TextView tv_DHTCNgay_KetQua, tv_BQDS_KetQua, tv_TongDoanhSo_KetQua, tv_rateGT_KetQua, tv_KHmoi_KetQua, tv_NVKD;

    int day, month, year;
    private ProgressDialog pDialog;
    Controller control;
    ConvertFont cf = new ConvertFont();
    int totalPage = 0, currentPage = 0, totalMoiTao = 0, currentPageBC = 0, totalPageBC = 0, total = 0, pageCount = 0;
    List<DonHang> listDonHang;
    List<String> listNVKD;
    String idNVKD;
    List<BCTongHop> listBCTongHop;
    List<NVKD> mList;
    int bDate = 0, totalKHDuocGiao = 0;

    List<Rpt_Kpi_Model> mListRptKPI;
    Rpt_Kpi_Adapter adapterKpi = null;

    String startDate;
    String endDate;
    RadioButton rad, rad1;
    RadioGroup radioGroup;
    boolean ckrad;
    LinearLayout linearLayout1;
    private Activity activity;

    private Get_RPT_KPI_Task get_rpt_kpi_task;

    public BaoCaoKPIFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_baocao_kpi
        control = new Controller(activity);

        View view = inflater.inflate(R.layout.fragment_rpt_kpi, container, false);
        linearLayout1 = (LinearLayout) view.findViewById(R.id.linearLayout1);
        btn_searchKPI = (Button) view.findViewById(R.id.btn_searchRPT2);

        sp_NVKD = (Spinner) view.findViewById(R.id.sp_NVKD2);
        tv_NVKD = (TextView) view.findViewById(R.id.tv_NVKD2);

        et_startDateKPI = (EditText) view.findViewById(R.id.et_startDate2);
        et_endDateKPI = (EditText) view.findViewById(R.id.et_endDate2);
        iv_startDateKPI = (ImageView) view.findViewById(R.id.iv_startDate2);
        iv_endDateKPI = (ImageView) view.findViewById(R.id.iv_endDate2);
        rad = (RadioButton) view.findViewById(R.id.radio_Ngay_KPI);
        rad1 = (RadioButton) view.findViewById(R.id.radio_Thang_KPI);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        getDate();

        // new DSNhaCungCapTask().execute();

        iv_startDateKPI.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (et_startDateKPI.getText().toString().length() > 0) {
                    String[] item = et_startDateKPI.getText().toString().split("/");
                    day = Integer.parseInt(item[0]);
                    month = Integer.parseInt(item[1]) - 1;
                    year = Integer.parseInt(item[2]);
                }
                DialogFragment dialog = new DatePickerFragment(0);
                dialog.show(getFragmentManager(), "DatePicker");
            }
        });

        iv_endDateKPI.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (et_endDateKPI.getText().toString().length() > 0) {
                    String[] item = et_endDateKPI.getText().toString().split("/");
                    day = Integer.parseInt(item[0]);
                    month = Integer.parseInt(item[1]) - 1;
                    year = Integer.parseInt(item[2]);
                }
                DialogFragment dialog = new DatePickerFragment(1);
                dialog.show(getFragmentManager(), "DatePicker");
            }
        });

        btn_searchKPI.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (linearLayout1.getVisibility() == View.INVISIBLE)
                    linearLayout1.setVisibility(View.VISIBLE);
                idNVKD = "";
                totalKHDuocGiao = 0;
                totalPage = total = currentPage = totalMoiTao = currentPageBC = totalPageBC = 0;
                if (control.isOnline(activity)) {
                    startDate = et_startDateKPI.getText().toString();
                    endDate = et_endDateKPI.getText().toString();
                    if (rad.isChecked()) {
                        ckrad = true;
                        endDate = startDate;
                    }
                    if (rad1.isChecked()) {
                        ckrad = false;
                        endDate = startDate;
                        String[] item1 = endDate.split("/");
                        startDate = "01/" + item1[1] + "/" + item1[2];
                    }
                    mListRptKPI = new ArrayList<Rpt_Kpi_Model>();
                    getListView().setAdapter(null);
                    if (get_rpt_kpi_task == null || get_rpt_kpi_task.getStatus() == AsyncTask.Status.FINISHED) {
                        get_rpt_kpi_task = new Get_RPT_KPI_Task();
                        get_rpt_kpi_task.execute();
                    }
                }
            }
        });
        return view;
    }

    class Get_RPT_KPI_Task extends AsyncTask<Void, List<Rpt_Kpi_Model>, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getResources().getText(R.string.load_data));
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(Void... params) {
            pageCount++;
            if (totalPage == 0) {
                totalPage = 100;
            }
            String url, json_dsbl = "";
            url = getURL_RPT_KPI();
            try {
                json_dsbl = control.jsonValues(url, false);
                Log.d(TAG + " json_dsbl", json_dsbl);
//				 json_dsbl = control.getDataJSON(url, false); //Khi null hay bi loi
                if (json_dsbl.contains("api_error")) {
                    mAF.writelog("Error KPI Rpt: " + json_dsbl, activity, mAF.filelog);
                } else {
                    Type listType = new TypeToken<List<Rpt_Kpi_Model>>() {
                    }.getType();
                    List<Rpt_Kpi_Model> ds_bl = new ArrayList<Rpt_Kpi_Model>();
                    try {
                        ds_bl = (List<Rpt_Kpi_Model>) new Gson().fromJson(json_dsbl, listType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ckrad && ds_bl.get(0).isHoliday() == false) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayout1.setVisibility(View.INVISIBLE);
                                Toast.makeText(activity, "Ngày được lựa chọn là ngày nghỉ!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    if (ds_bl.size() > 0) {
                        for (int i = 0; i < ds_bl.size(); i++) {
                            if (ckrad && i == 4) {
                                mListRptKPI.add(new Rpt_Kpi_Model(ds_bl.get(i).getKpi_target_name(), null, null, null, null));
                            } else if (ckrad && i == 5) {
                                mListRptKPI.add(new Rpt_Kpi_Model(ds_bl.get(i).getKpi_target_name(), null, ds_bl.get(i).getThuc_te(), null, null));
                            } else {
                                mListRptKPI.add(ds_bl.get(i));
                            }
                            Log.d(TAG + " ds_dl", ds_bl.get(i).toString());
                        }
                    }
                }
            } catch (Exception ex) {
                mAF.writelog("Error KPI Rpt: " + ex.toString(), activity, mAF.filelog);
            }
            publishProgress(mListRptKPI);
            return null;
        }


        private String getURL_RPT_KPI() {
            String url = API_code.URL_BC_KPI + "&pageno=" + pageCount + "&pagerec=100&org_id=0" + "&assign_id=0" + "&json={\"date1\":\"" + startDate + "\"" + ",\"date2\":\""
                    + endDate + "\"" + "}";
            Log.i(TAG + " cuong URL_BC_KPI", url);
            return url;
        }

        @Override
        protected void onProgressUpdate(List<Rpt_Kpi_Model>... values) {
            super.onProgressUpdate(values);
            if (adapterKpi == null) {
                adapterKpi = new Rpt_Kpi_Adapter(activity, values[0]);
                setListAdapter(adapterKpi);
            } else {
                // adapter.notifyDataSetChanged();
                // cuongtm them
                adapterKpi = new Rpt_Kpi_Adapter(activity, values[0]);
                setListAdapter(adapterKpi);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissWithCheck(pDialog);
        }
    }

    //
    private class ResultSearchTask extends AsyncTask<DonHangTKC, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Đang tìm kiếm ...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(DonHangTKC... params) {
            // TODO Auto-generated method stub
            currentPage++;
            String json = new Gson().toJson(params);
            if (totalPage == 0) {
                String url = API_code.URL_DS_DONDATHANG + "&pageno=-1&json=" + control.subJSON(json);
                NorAndNop n = control.getNorNop(url);
                totalPage = Integer.parseInt(n.getNop());

                if (totalPage == 0) {
                    return null;
                }
            }

            String url = API_code.URL_DS_DONDATHANG + "&pageno=" + currentPage + "&json=" + control.subJSON(json);
            String jsonData = control.getDataJSON(url, false);
            Type listType = new TypeToken<List<DonHang>>() {
            }.getType();
            List<DonHang> listData = new ArrayList<DonHang>();
            try {
                listData = new Gson().fromJson(jsonData, listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (listData.size() > 0) {
                for (int i = 0; i < listData.size(); i++) {
                    if (!listData.get(i).getTrangthai().equals("1") && !listData.get(i).getTrangthai().equals("5") && !listData.get(i).getTrangthai().equals("9")) {
                        listDonHang.add(listData.get(i));
                    }
                }
            }
            if (currentPage < totalPage) {
                doInBackground(params);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissWithCheck(pDialog);
            if (listDonHang.size() > 0) {
                for (int i = 0; i < listDonHang.size(); i++) {
                    String tien = listDonHang.get(i).getTongtien().replace(",", "");
                    int tien_donhang = Integer.parseInt(tien);
                    total += tien_donhang;
                }
                DecimalFormat formatterPrice = new DecimalFormat("#,###,###");
                DecimalFormat formatter = new DecimalFormat("####.##");
                String doanhso = formatterPrice.format(Long.parseLong(String.valueOf(total)));
                tv_TongDoanhSo_KetQua.setText(doanhso);
                tv_DHTCNgay_KetQua.setText(formatter.format(listDonHang.size() / ((double) bDate)));
            }
        }
    }

    private class BCTongHopTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            currentPageBC++;
            if (totalPageBC == 0) {
                String url = API_code.URL_BC_TONGHOP + "&pageno=-1";
                NorAndNop n = control.getNorNop(url);
                totalPageBC = Integer.parseInt(n.getNop());
                if (totalPageBC == 0) {
                    return null;
                }

                // Get totalKH duoc giao
                String urlKHChuaGheTham = API_code.URL_GHETHAM_DSKH + "&pageno=-1";
                NorAndNop t = control.getNorNop(urlKHChuaGheTham);
                totalKHDuocGiao = Integer.parseInt(t.getNor());
            }

            String url = API_code.URL_BC_TONGHOP + "&pageno=" + currentPageBC + "&json={\"nvkd\":\"" + params[0] + "\",\"bd\":" + params[1] + ",\"kt\":" + params[2]
                    + "}";
            String jsonData = control.getDataJSON(url, false);
            Type listType = new TypeToken<List<BCTongHop>>() {
            }.getType();
            List<BCTongHop> listData = new ArrayList<BCTongHop>();
            try {
                listData = new Gson().fromJson(jsonData, listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (listData.size() > 0) {
                for (int i = 0; i < listData.size(); i++) {
                    listBCTongHop.add(listData.get(i));
                }
            }
            if (currentPageBC < totalPageBC) {
                doInBackground(params);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dismissWithCheck(pDialog);
            if (listBCTongHop.size() > 0) {
                DecimalFormat formatterPrice = new DecimalFormat("#,###,###");
                DecimalFormat formatter = new DecimalFormat("####.##");
                if (listDonHang.size() > 0) {
                    tv_BQDS_KetQua.setText(formatterPrice.format(Math.round(total / listDonHang.size())));
                } else {
                    if (total != 0) {
                        tv_BQDS_KetQua.setText(formatterPrice.format(Math.round(total)));
                    }
                }

                if (sp_NVKD.getSelectedItemPosition() == 0) {
                    int khmoi = 0;
                    int checkin = 0;
                    for (int i = 0; i < listBCTongHop.size(); i++) {
                        if (!listBCTongHop.get(i).no_new.equals("")) {
                            khmoi += Integer.parseInt(listBCTongHop.get(i).no_new);
                        }
                        if (!listBCTongHop.get(i).no_checkin.equals("")) {
                            checkin += Integer.parseInt(listBCTongHop.get(i).no_checkin);

                        }
                    }
                    tv_rateGT_KetQua.setText(formatter.format(checkin / (double) (totalKHDuocGiao + checkin) * 100) + "%");
                    tv_KHmoi_KetQua.setText("" + khmoi);
                } else {
                    for (int i = 0; i < listBCTongHop.size(); i++) {
                        String s = idNVKD;
                        String s1 = listBCTongHop.get(i).seller;
                        if (s.equals(s1)) {
                            tv_rateGT_KetQua.setText(listBCTongHop.get(i).no_checkin);
                            tv_KHmoi_KetQua.setText(listBCTongHop.get(i).no_new);
                            if (!listBCTongHop.get(i).no_checkin.equals("")) {
                                int checkin = Integer.parseInt(listBCTongHop.get(i).no_checkin);
                                double rate = checkin / (double) (totalKHDuocGiao + checkin);
                                tv_rateGT_KetQua.setText(formatter.format(rate * 100) + "%");
                            }
                        }
                    }
                }
            }
        }
    }

    private class DSNhaCungCapTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String json = control.getDataJSON(API_code.URL_DS_NVKD, false);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            listNVKD = new ArrayList<String>();
            Type listType = new TypeToken<List<NVKD>>() {
            }.getType();
            mList = new Gson().fromJson(result, listType);
            listNVKD.add("Tất cả");
            for (int i = 0; i < mList.size(); i++) {
                listNVKD.add(cf.getUTF8StringFromNCR(mList.get(i).user_name));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, listNVKD);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            sp_NVKD.setAdapter(adapter);
        }
    }

    private void getDate() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        et_startDateKPI.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
        et_endDateKPI.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        int id;

        public DatePickerFragment(int id) {
            // TODO Auto-generated constructor stub
            this.id = id;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            DatePickerDialog dialog = new DatePickerDialog(activity, this, year, month, day);
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int selectyear, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            year = selectyear;
            month = monthOfYear;
            day = dayOfMonth;
            switch (id) {
                case 0:
                    et_startDateKPI.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
                    break;
                case 1:
                    et_endDateKPI.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void dismissWithCheck(Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dismissWithTryCatch(dialog);
                        }
                    } else {

                        // Api < 17. Unfortunately cannot check for isDestroyed()
                        if (!((Activity) context).isFinishing()) {
                            dismissWithTryCatch(dialog);
                        }
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithTryCatch(dialog);
            }
            dialog = null;
        }
    }

    public void dismissWithTryCatch(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }
}