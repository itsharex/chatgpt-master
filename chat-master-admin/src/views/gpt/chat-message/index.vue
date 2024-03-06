<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="角色模型" prop="role">
        <el-input v-model="queryParams.role" placeholder="请选择角色" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="调用token" prop="openaiApiId">
        <el-input v-model="queryParams.openaiApiId" placeholder="请输入调用token" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['gpt:chat:message:save']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['gpt:chat:message:update']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['gpt:chat:message:remove']">删除</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gpt:chat:message:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="聊天摘要" align="center" prop="chatTitle">
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.chatTitle" placement="top">
            <div class="table-cell"> {{ scope.row.chatTitle }} </div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="消息内容" align="center" prop="content">
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.content" placement="top">
            <div class="table-cell"> {{ scope.row.content }} </div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="角色模型" align="center" prop="role" />
      <el-table-column label="结束原因" align="center" prop="finishReason" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dictTag :options="dict.type.gpt_chat_status" :value="scope.row.status"></dictTag>
        </template>
      </el-table-column>
      <el-table-column label="响应全文" align="center" prop="response">
        <template slot-scope="scope">
          <div class="table-cell"> {{ scope.row.response }} </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['gpt:chat:message:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.current" :limit.sync="queryParams.size"
      @pagination="getList" />

    <!-- 添加或修改对话消息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="chat_id" prop="chatId">
          <el-input v-model="form.chatId" placeholder="请输入chat_id" />
        </el-form-item>
        <el-form-item label="消息内容">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="role" prop="role">
          <el-input v-model="form.role" placeholder="请输入role" />
        </el-form-item>
        <el-form-item label="openai_api_id" prop="openaiApiId">
          <el-input v-model="form.openaiApiId" placeholder="请输入openai_api_id" />
        </el-form-item>
        <el-form-item label="finish_reason" prop="finishReason">
          <el-input v-model="form.finishReason" placeholder="请输入finish_reason" />
        </el-form-item>
        <el-form-item label="响应全文" prop="response">
          <el-input v-model="form.response" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  pageChatMessage,
  listChatMessage,
  getChatMessage,
  addChatMessage,
  updateChatMessage,
  delChatMessage,
} from "@/api/gpt/chatMessage";

export default {
  name: "ChatMessage",
  dicts: ['gpt_chat_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 对话消息表格数据
      messageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        chatId: null,
        content: null,
        role: null,
        openaiApiId: null,
        finishReason: null,
        status: null,
        response: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        createUser: [
          { required: true, message: "创建人不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询对话消息列表 */
    getList() {
      this.loading = true;
      pageChatMessage(this.queryParams).then(res => {
        this.messageList = res.data.records;
        this.total = res.data.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        createUser: null,
        createTime: null,
        chatId: null,
        content: null,
        role: null,
        openaiApiId: null,
        finishReason: null,
        status: 0,
        response: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.current = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加对话消息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getChatMessage(id).then(res => {
        this.form = res.data;
        this.open = true;
        this.title = "修改对话消息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateChatMessage(this.form).then(res => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addChatMessage(this.form).then(res => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除对话消息编号为"' + ids + '"的数据项？').then(function () {
        return delChatMessage(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/gpt/chat-message/export', {
        ...this.queryParams
      }, `message_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style scoped>
.table-cell {
  height: 40px;
  line-height: 40px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>