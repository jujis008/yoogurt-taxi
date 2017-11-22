package com.yoogurt.taxi.account.controller.web;

import com.yoogurt.taxi.account.form.UpdateBillForm;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.excel.BankReceiptOfMerchantsModel;
import com.yoogurt.taxi.common.helper.excel.ExcelData;
import com.yoogurt.taxi.common.helper.excel.ExcelHeader;
import com.yoogurt.taxi.common.helper.excel.ExcelUtils;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.condition.account.BillListWebCondition;
import com.yoogurt.taxi.dal.condition.account.ExportBillCondition;
import com.yoogurt.taxi.dal.condition.account.WithdrawListWebCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.TradeType;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillDetailModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("web/account")
public class FinanceWebController extends BaseController {
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private FinanceBillService financeBillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getAccountListWeb(AccountListWebCondition condition) {
        BasePager<FinanceAccountListModel> listWeb = financeAccountService.getListWeb(condition);
        return ResponseObj.success(listWeb);
    }

    @RequestMapping(value = "/bill/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getBillListWeb(BillListWebCondition condition) {
        BasePager<FinanceBillListWebModel> financeBillListWeb = financeBillService.getFinanceBillListWeb(condition);
        return ResponseObj.success(financeBillListWeb);
    }

    @RequestMapping(value = "/withdraw/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getWithdrawBillListWeb(WithdrawListWebCondition condition) {
        BasePager<WithdrawBillListWebModel> withdrawBillListWeb = financeBillService.getWithdrawBillListWeb(condition);
        return ResponseObj.success(withdrawBillListWeb);
    }

    @RequestMapping(value = "/withdraw/billId/{billId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getWithdrawBillDetail(@PathVariable(name = "billId") Long billId) {
        WithdrawBillDetailModel withdrawBillDetail = financeBillService.getWithdrawBillDetail(billId);
        return ResponseObj.success(withdrawBillDetail);
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj handleWithdraw(@RequestBody @Valid UpdateBillForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        return financeAccountService.handleWithdraw(form.getId(), BillStatus.getEnumsByCode(form.getStatus()));
    }

    //TODO 上线时，切换成需要后台登录
    @RequestMapping(value = "/i/withdraw/download", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public void dowmLoadWithdrawExcel(ExportBillCondition condition, HttpServletResponse response) {
        List<ExcelData> excelDataList = new ArrayList<>();
        // 设置第一个表数据
        ExcelData excelData = new ExcelData();
        // 设置第一个sheet表名
        excelData.setSheetName("待处理提现工单");
        List<ExcelHeader> excelHeaders = new ArrayList<>();
        excelHeaders.add(new ExcelHeader("payeeAccount", "账号", 1));
        excelHeaders.add(new ExcelHeader("payeeName", "户名", 2));
        excelHeaders.add(new ExcelHeader("amount", "金额", 3));
        excelHeaders.add(new ExcelHeader("bankName", "开户行", 4));
        excelHeaders.add(new ExcelHeader("bankAddress", "开户地", 5));
        excelHeaders.add(new ExcelHeader("id", "注释", 6));

        excelHeaders.sort(Comparator.comparingInt(ExcelHeader::getOrder));
        excelData.setExcelHeaders(excelHeaders);

        //目前只支持提现至银行卡
        condition.setBillStatus(BillStatus.PENDING.getCode());
        condition.setTradeType(TradeType.WITHDRAW.getCode());
        condition.setDestinationType(DestinationType.BANK.getCode());
        List<Map<String, Object>> billList = financeBillService.getBillListForExport(condition);
        if (CollectionUtils.isNotEmpty(billList)) {
            ExcelUtils.createAndDownExcel("提现工单", excelDataList, response);
            excelData.setBodyDatas(billList);

            excelDataList.add(excelData);
            ExcelUtils.createAndDownExcel("提现工单", excelDataList, response);
        }
    }

    //TODO 上线时，切换成需要后台登录
    @RequestMapping(value = "/i/withdraw/import", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj importExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<BankReceiptOfMerchantsModel> list = ExcelUtils.importExcelForWithdraw(file.getInputStream());
        return financeBillService.batchHandleWithdraw(list);
    }
}
