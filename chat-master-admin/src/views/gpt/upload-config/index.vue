<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="配置名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入配置名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gpt:upload:config:save']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gpt:upload:config:update']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gpt:upload:config:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="配置名称" align="center" prop="title" />
      <el-table-column label="覆盖同名文件" align="center" prop="uploadReplace" />
      <el-table-column label="缩图开关" align="center" prop="thumbStatus" />
      <el-table-column label="缩放宽度" align="center" prop="thumbWidth" />
      <el-table-column label="缩放高度" align="center" prop="thumbHeight" />
      <el-table-column label="缩图方式" align="center" prop="thumbType" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gpt:upload:config}:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gpt:upload:config:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.current"
      :limit.sync="queryParams.size"
      @pagination="getList"
    />

    <!-- 添加或修改缩略图配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="配置名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="覆盖同名文件" prop="uploadReplace">
          <el-input v-model="form.uploadReplace" placeholder="请输入覆盖同名文件" />
        </el-form-item>
        <el-form-item label="缩放宽度" prop="thumbWidth">
          <el-input v-model="form.thumbWidth" placeholder="请输入缩放宽度" />
        </el-form-item>
        <el-form-item label="缩放高度" prop="thumbHeight">
          <el-input v-model="form.thumbHeight" placeholder="请输入缩放高度" />
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
    pageUploadConfig,
    listUploadConfig,
    getUploadConfig,
    addUploadConfig,
    updateUploadConfig,
    delUploadConfig,
  } from "@/api/gpt/uploadConfig";

  export default {
  name: "UploadConfig",
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
      // 缩略图配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        current: 1,
        size: 10,
        title: null,
        uploadReplace: null,
        thumbStatus: null,
        thumbWidth: null,
        thumbHeight: null,
        thumbType: null
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
        updateUser: [
          { required: true, message: "更新人不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询缩略图配置列表 */
    getList() {
      this.loading = true;
      pageUploadConfig(this.queryParams).then(res => {
        this.configList = res.data.records;
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
        updateUser: null,
        updateTime: null,
        title: null,
        uploadReplace: null,
        thumbStatus: 0,
        thumbWidth: null,
        thumbHeight: null,
        thumbType: null
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加缩略图配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getUploadConfig(id).then(res => {
        this.form = res.data;
        this.open = true;
        this.title = "修改缩略图配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateUploadConfig(this.form).then(res => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUploadConfig(this.form).then(res => {
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
      this.$modal.confirm('是否确认删除缩略图配置编号为"' + ids + '"的数据项？').then(function() {
        return delUploadConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/gpt/upload-config/export', {
        ...this.queryParams
      }, `config_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
