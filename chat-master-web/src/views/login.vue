<script setup lang='ts'>
import { computed, ref } from 'vue'
import { NForm, FormInst, NFormItem, FormItemRule, NInput, NCheckbox, NButton, useMessage } from 'naive-ui'
import { fetchVerify } from '@/api'
import { useAuthStore } from '@/store'
import logoLeft from '@/assets/logo-left.jpg'
import indexBack from '@/assets/media/index-back.mp4'
import { router } from '@/router'

const authStore = useAuthStore()
const ms = useMessage()

const visible = ref(false);
const loading = ref(false)
const formRef = ref<FormInst | null>(null)

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
  tel: '',
  password: '',
  checked: false,
  loginType: 3
})

const disabled = computed(() => {
  return !(loginValue.value?.tel.trim() && loginValue.value?.password.trim() && !loading.value && loginValue.value?.checked)
})

const rules = {
  tel: {
    required: true,
    pattern: /^[1][3456789]\d{9}$/,
    message: '请输入正确的手机号码',
    trigger: ['input', 'blur']
  },
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: ['input', 'blur']
    },
    {
      trigger: ["blur", "change"],
      pattern: /^(?=^.*?[a-z])(?=^.*?\d).{6,32}$/,
      message: "请输入6-32位包含字母和数字的密码",
    },
  ],
  checked: {
    required: true,
    message: '请勾选“用户协议与隐私政策”',
    trigger: ['input', 'blur', 'change'],
    validator(rule: FormItemRule, value: boolean) {
      if (!value) {
        return new Error('请勾选“用户协议与隐私政策”')
      }
      return true
    },
  },
}

/** 立即体验 */
function handlePress() {
  visible.value = !visible.value;
}

/** 登录 */
async function handleVerify(e: MouseEvent) {
  e.preventDefault();
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return;
    }
    try {
      loading.value = true;
      fetchVerify<Token>(loginValue.value).then(res => {
        if (res.code === 200) {
          authStore.setToken(res.data.token)
          ms.success('登录成功')
          router.push("/")
        } else {
          ms.error(res.msg ?? '登录失败')
        }
      })
    } finally {
      loading.value = false
    }
  })
}
</script>
<style scoped>
.login-form {
  height: 45px;
  border-radius: 8px;
  display: flex;
  align-items: center;
}

.slide-left {
  -webkit-animation: slide-left 0.5s cubic-bezier(0.250, 0.460, 0.450, 0.940) both;
  animation: slide-left 0.5s cubic-bezier(0.250, 0.460, 0.450, 0.940) both;
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
    <div class="w-full h-full p-8 relative box-border">
      <video autoplay loop muted class="w-full h-full absolute object-cover top-0 left-0">
        <source :src="indexBack" type="video/mp4" />
      </video>
      <div class="w-full h-full z-1 fixed">
        <div class="w-full flex items-center">
          <img :src="logoLeft" />
          <span class="text-[50px] font-medium">{{ $t('common.siteTitle') }}</span>
        </div>
        <div class="w-full h-full  flex mt-[100px]">
          <div class="w-[800px] ml-[350px]">
            <div class="flex items-center"><img :src="logoLeft" /> <span class="text-[50px] font-medium">{{ $t('common.siteTitle') }}</span></div>
            <div class="text-[70px] font-bold ml-[30px]">{{ $t('login.title') }}</div>
            <div class="text-[20px] font-light ml-[30px]">{{ $t('login.subTitle') }}</div>
            <div
              class="w-[196px] h-[60px] mt-[80px] ml-[30px] bg-[#2454ff] text-white text-[22px] flex items-center justify-center rounded-lg cursor-pointer  hover:bg-blue-700"
              @click="handlePress">
              {{ $t('login.quickStart') }} </div>
          </div>
          <div v-if="visible">
            <div class="w-[500px] h-[450px] py-6 px-10 ml-[150px] bg-white  rounded-xl slide-left">
              <NForm ref="formRef" :model="loginValue" :rules="rules">
                <div class="flex justify-center items-center mb-[18px] h-[65px]">
                  <img :src="logoLeft" class="w-[80px] h-[80px]"/>
                  <span class="text-[50px] font-medium">{{ $t('common.siteTitle') }}</span>
                </div>
                <div>
                  <NFormItem label="手机号" path="tel">
                    <NInput class="login-form" v-model:value="loginValue.tel" placeholder="请输入手机号" />
                  </NFormItem>
                </div>
                <div>
                  <NFormItem label="密码" path="password">
                    <NInput class="login-form" v-model:value="loginValue.password" type="password" placeholder="请输入密码" />
                  </NFormItem>
                </div>
                <div class="mb-1">
                  <NFormItem :show-label="false" path="checked">
                    <NCheckbox v-model:checked="loginValue.checked" />
                    <span class="ml-2 text-slate-700">{{ $t('login.checkTips') }}《用户协议》与《隐私政策》</span>
                  </NFormItem>
                </div>
                <NButton class="login-form" block type="primary" :disabled="disabled" :loading="loading"
                  @click="handleVerify">
                  {{ $t('login.login') }}
                </NButton>
              </NForm>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
