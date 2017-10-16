package com.yoogurt.taxi.account.controller.web;

import com.yoogurt.taxi.account.form.UpdateBillForm;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.condition.account.BillListWebCondition;
import com.yoogurt.taxi.dal.condition.account.WithdrawListWebCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillDetailModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("web/account")
public class FinanceWebController extends BaseController{
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private FinanceBillService  financeBillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getAccountListWeb(AccountListWebCondition condition) {
        Pager<FinanceAccountListModel> listWeb = financeAccountService.getListWeb(condition);
        return ResponseObj.success(listWeb);
    }

    @RequestMapping(value = "/bill/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getBillListWeb(BillListWebCondition condition) {
        Pager<FinanceBillListWebModel> financeBillListWeb = financeBillService.getFinanceBillListWeb(condition);
        return ResponseObj.success(financeBillListWeb);
    }

    @RequestMapping(value = "/withdraw/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getWithdrawBillListWeb(WithdrawListWebCondition condition) {
        Pager<WithdrawBillListWebModel> withdrawBillListWeb = financeBillService.getWithdrawBillListWeb(condition);
        return ResponseObj.success(withdrawBillListWeb);
    }

    @RequestMapping(value = "/withdraw/billId/{billId}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getWithdrawBillDetail(@PathVariable(name = "billId") Long billId) {
        WithdrawBillDetailModel withdrawBillDetail = financeBillService.getWithdrawBillDetail(billId);
        return ResponseObj.success(withdrawBillDetail);
    }

    @RequestMapping(value = "/withdraw",method = RequestMethod.PATCH,produces = {"application/json;charset=utf-8"})
    public ResponseObj updateWithdrawBillStatus(@RequestBody @Valid UpdateBillForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        int i = financeBillService.updateStatus(form.getId(), BillStatus.getEnumsByCode(form.getStatus()));
        if (i >0) {
            return ResponseObj.success();
        }
        return ResponseObj.fail();
    }
}
