<script setup lang='ts'>
import { computed, onMounted, ref } from "vue";
import {
  NForm,
  FormInst,
  NFormItem,
  FormItemRule,
  NInput,
  NCheckbox,
  NButton,
  useMessage
} from "naive-ui";
import { fetchVerify, fetchChatConfig } from "@/api";
import { useAppStore, useAuthStore } from "@/store";
import { useBasicLayout } from "@/hooks/useBasicLayout";
import logo from "@/assets/logo.jpg";
import indexBack from "@/assets/media/index-back.mp4";
import { router } from "@/router";

const authStore = useAuthStore();
const appStore = useAppStore();
const ms = useMessage();
const { isMobile } = useBasicLayout();

const visible = ref(false);
const loading = ref(false);
const formRef = ref<FormInst | null>(null);
const baseInfo = ref<any>(null);

interface Token {
  token: string;
  refreshToken: string;
  expiresIn: number;
}

interface Login {
  tel: string;
  password: string;
  checked: false;
  loginType: number;
}
const loginValue = ref<Login>({
  tel: "",
  password: "",
  checked: false,
  loginType: 3
});

const disabled = computed(() => {
  return !(
    loginValue.value?.tel.trim() &&
    loginValue.value?.password.trim() &&
    !loading.value &&
    loginValue.value?.checked
  );
});

const rules = {
  tel: {
    required: true,
    pattern: /^[1][3456789]\d{9}$/,
    message: "请输入正确的手机号码",
    trigger: ["input", "blur"]
  },
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: ["input", "blur"]
    },
    {
      trigger: ["blur", "change"],
      pattern: /^(?=^.*?[a-z])(?=^.*?\d).{6,32}$/,
      message: "请输入6-32位包含字母和数字的密码"
    }
  ],
  checked: {
    required: true,
    message: "请勾选“用户协议与隐私政策”",
    trigger: ["input", "blur", "change"],
    validator(rule: FormItemRule, value: boolean) {
      if (!value) {
        return new Error("请勾选“用户协议与隐私政策”");
      }
      return true;
    }
  }
};

onMounted(() => {
  if (isMobile.value) {
    visible.value = true;
  }
  fetchChatConfig<any>().then(res => {
    if (res.code === 200) {
      baseInfo.value = res.data.baseInfo;
      appStore.setBaseInfo(res.data.baseInfo);
    }
  });
});

/** 立即体验 */
function handlePress() {
  visible.value = !visible.value;
}

/** 登录 */
async function handleVerify(e: MouseEvent) {
  e.preventDefault();
  formRef.value?.validate(async errors => {
    if (errors) {
      return;
    }
    try {
      loading.value = true;
      fetchVerify<Token>(loginValue.value).then(res => {
        if (res.code === 200) {
          authStore.setToken(res.data.token);
          ms.success("登录成功");
          router.push("/");
        } else {
          ms.error(res.msg ?? "登录失败");
        }
      });
    } finally {
      loading.value = false;
    }
  });
}

// 跳转页面
function jumpToXieyi(type: number) {
  const { href } = router.resolve({
    path: "/agreement",
    query: { type: type }
  });
  // 打开新窗口
  window.open(href);
}

const getMobileClass = computed(() => {
  if (isMobile.value) return [""];
  return ["slide-left", "ml-[200px]","w-[500px]"];
});
</script>
<style scoped>
.login-form {
  height: 45px;
  border-radius: 8px;
  display: flex;
  align-items: center;
}

.slide-left {
  -webkit-animation: slide-left 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
  animation: slide-left 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
}

/**
 * ----------------------------------------
 * animation slide-left
 * ----------------------------------------
 */
@-webkit-keyframes slide-left {
  0% {
    -webkit-transform: translateX(0);
    transform: translateX(0);
  }

  100% {
    -webkit-transform: translateX(-100px);
    transform: translateX(-100px);
  }
}

@keyframes slide-left {
  0% {
    -webkit-transform: translateX(0);
    transform: translateX(0);
  }

  100% {
    -webkit-transform: translateX(-100px);
    transform: translateX(-100px);
  }
}
</style>

<template>
  <div class="bg-[#ebf3fe] w-full h-full overflow-auto">
    <div class="w-full h-full relative box-border">
      <video autoplay loop muted class="w-full h-full absolute object-cover top-0 left-0">
        <source :src="indexBack" type="video/mp4" />
      </video>
      <div class="w-full h-full z-1 fixed">
        <div v-if="!isMobile" class="w-full flex items-center pt-6 pl-6">
          <img
            :src="baseInfo && baseInfo.siteLogo ? baseInfo.siteLogo: logo"
            class="w-[122px] h-[122px]"
          />
          <span
            class="text-[50px] font-medium"
          >{{ baseInfo && baseInfo.siteTitle ? baseInfo.siteTitle: $t('common.siteTitle') }}</span>
        </div>
        <div class="w-full h-full flex mt-[100px]">
          <div v-if="!isMobile" class="ml-[350px] max-w-[800px]">
            <!-- <div class="flex items-center"><span class="text-[50px] font-medium">{{ $t('common.siteTitle') }}</span></div> -->
            <div class="text-[70px] font-bold">{{ '一键切换' }}</div>
            <div class="text-[70px] font-bold">{{ $t('login.title') }}</div>
            <div class="text-[20px] font-light">{{ $t('login.subTitle') }}</div>
            <div
              class="w-[196px] h-[60px] mt-[80px] bg-[#2454ff] text-white text-[22px] flex items-center justify-center rounded-lg cursor-pointer hover:bg-blue-700"
              @click="handlePress"
            >{{ $t('login.quickStart') }}</div>
          </div>
          <div v-if="visible">
            <div class="py-6 px-10 bg-white rounded-xl" :class="getMobileClass">
              <NForm ref="formRef" :model="loginValue" :rules="rules">
                <div class="flex justify-center items-center mb-[18px] h-[65px]">
                  <img
                    :src="baseInfo && baseInfo.siteLogo ? baseInfo.siteLogo: logo"
                    class="w-[80px] h-[80px]"
                  />
                  <span
                    class="text-[40px] font-medium"
                  >{{ baseInfo && baseInfo.siteTitle ? baseInfo.siteTitle: $t('common.siteTitle') }}</span>
                </div>
                <div>
                  <NFormItem label="手机号" path="tel">
                    <NInput class="login-form" v-model:value="loginValue.tel" placeholder="请输入手机号" />
                  </NFormItem>
                </div>
                <div>
                  <NFormItem label="密码" path="password">
                    <NInput
                      class="login-form"
                      v-model:value="loginValue.password"
                      type="password"
                      placeholder="请输入密码"
                    />
                  </NFormItem>
                </div>
                <div class="mb-1">
                  <NFormItem :show-label="false" path="checked">
                    <NCheckbox v-model:checked="loginValue.checked" />
                    <span class="ml-2 text-slate-700">
                      {{ $t('login.checkTips') }}
                      <span
                        @click="jumpToXieyi(1)"
                        class="text-blue-600 cursor-pointer"
                      >{{ $t('common.userXieyi') }}</span> 与
                      <span
                        @click="jumpToXieyi(2)"
                        class="text-blue-600 cursor-pointer"
                      >{{ $t('common.privacyZhengce') }}</span>
                    </span>
                  </NFormItem>
                </div>
                <NButton
                  class="login-form"
                  block
                  type="primary"
                  :disabled="disabled"
                  :loading="loading"
                  @click="handleVerify"
                >{{ $t('login.login') }}</NButton>
              </NForm>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
