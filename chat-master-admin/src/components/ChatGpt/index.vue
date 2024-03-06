<template>
    <div>
        <div class="chart-icon" id="chart-icon" @click="showCall">
            <svg-icon icon-class="wechat" class="icon" id="wechat-icon" style="pointer-events: none" />
            <div class="unRead" v-if="!show && unreadCount > 0" id="read-icon">
                {{ unreadCount }}
            </div>
        </div>
        <div class="chatgpt-dialog" v-show="show">
            <div class="chatgpt-dialog-header">
                <div class="chatgpt-dialog-title">ChatGpt Chatbot</div>
                <div class="chatgpt-dialog-close" @click="closeDialog">X</div>
            </div>
            <div class="chatgpt-dialog-content">
                <div class="chatgpt-dialog-session">
                    <!-- 左侧问题列表 -->
                    <ul>
                        <li class="chatgpt-dialog-session-new-session" @click="addNewQuestion">新增会话</li>
                        <li class="chatgpt-dialog-session-question" @click="clickQuestion" v-for="question in questions"
                            :key="question.id" :id="`question-${question.id}`">
                            <i class="el-icon-chat-dot-square"></i>
                            <span class="chatgpt-dialog-session-question-content" :contenteditable="question.editable">{{
                                question.name }}</span>
                            <span class="chatgpt-dialog-session-question-actions">
                                <i class="el-icon-edit" @click="editQuestion(question)"></i>
                                <i class="el-icon-delete" @click="removeQuestion(question.id)"></i>
                                <i class="el-icon-check" v-show="question.editable" @click="saveQuestion(question)"></i>
                            </span>
                        </li>
                    </ul>
                </div>
                <div class="chatgpt-dialog-chat">
                    <div class="chatgpt-dialog-messages" id="chat-messages">
                        <!-- 右侧聊天列表 -->
                        <div v-if="messages && messages.length > 0">
                            <div v-for="message in messages" :key="message.id" class="chatgpt-dialog-message">
                                <div class="chatgpt-dialog-message-content">
                                    <div class="chatgpt-dialog-avatar">
                                        <el-avatar icon="el-icon-user-solid"></el-avatar>
                                    </div>
                                    <div class="chatgpt-dialog-message-text" v-html="message.text"></div>
                                </div>
                            </div>
                        </div>
                        <!-- 模版内容 -->
                        <div v-else class="chatgpt-dialog-template">
                            <!-- 在这里写入模版内容 -->
                            <!-- 标题栏 -->
                            <div class="chatgpt-dialog-template-header">
                                <div class="chatgpt-dialog-template-title">智能聊天机器人</div>
                                <div class="chatgpt-dialog-template-subtitle">
                                    <span>欢迎向我提问</span>
                                    <span class="chatgpt-dialog-template-refresh" @click="refreshTemplate">换一批 <i
                                            class="el-icon-refresh"></i></span>

                                </div>
                            </div>

                            <!-- 卡片列表 -->
                            <div class="chatgpt-dialog-template-cards">
                                <div v-for="card in templateCards" :key="card" class="chatgpt-dialog-template-card"
                                    @click="handleCardClick(card)">
                                    <div class="chatgpt-dialog-template-card-text">
                                        {{ card }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="chatgpt-dialog-input">
                        <!-- 输入框 -->
                        <input type="text" v-model="newMessage" @keyup.enter="sendMessage">
                        <!-- 发送按钮 -->
                        <button class="chatgpt-dialog-send" @click="sendMessage">
                            <i class="el-icon-s-promotion"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: "ChatGpt",
    props: {
        apiToken: {
            type: String,
            required: true
        }
    },
    data() {
        return {
            questions: [],
            messages: [],
            messagesTemplate: [
                "一个半小时是几个半小时呢？",
                "单身很久的人怎么找到女朋友？",
                "女朋友出轨了怎么办？",
                "如何看待996？",
                "如何在一个小县城内快速找到合适的工作？",
                "写一份后端开发的周报",
                "面试时工资探底了怎么办？",
                "给我写一遍疯狂星期四V50的梗",
                "女朋友第一次给了我，相处过一年后发现实在不合适想分手女朋友已死相逼怎么办？",
                "谈恋爱是不是一定要睡？",
                "如果女朋友问，她和我老妈掉进水里，先救谁，我该怎么回答",
                "如何不动声色的对付绿茶？",
                "一定要问女朋友的问题有哪些？",
                "请帮我安排一个科学减肥30斤的方案",
                "单身很久的人怎么找到女朋友？",
            ],
            templateCards: [],
            newMessage: '',
            show: false,
            //展示的未读消息数量
            unreadCount: 0,
        }
    },
    created() {
        this.initData();
    },
    mounted() {
        // Automatically scroll to bottom of chat messages
        // this.$nextTick(() => {
        //     this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight;
        // });
    },
    methods: {
        // 展开会话
        showCall() {
            if (this.show) {
                this.show = false;
            } else {
                this.unreadCount = 0;
                this.show = true;
            }
        },
        // 初始化数据
        initData() {
            this.refreshTemplate();
        },
        // 关闭对话框
        closeDialog() {
            this.show = false;

        },
        addNewQuestion() {
            const newQuestion = {
                id: this.generateUUID(), // 假设你已经引入了uuid库
                name: 'New Chat',
                editable: false,
                selected: true // 新增的 selected 属性
            };
            this.questions.unshift(newQuestion);
            // 获取新添加的对话列表项
            let a = `question-${newQuestion.id}`;
            this.$nextTick(() => {
                const newQuestionEl = document.getElementById(a);
                // 先移除所有对话列表项的 selected 类名
                document.querySelectorAll('.chatgpt-dialog-session li:nth-child(n+2)').forEach(li => {
                    li.classList.remove('selected');
                });
                // 为当前点击的对话列表项添加 selected 类名
                newQuestionEl.classList.add('selected');
                // 然后触发对话框切换事件，显示对应的对话框
                // this.switchDialog(newQuestion.id);

            })

        },
        clickQuestion() {
            const lis = document.querySelectorAll('.chatgpt-dialog-session li:nth-child(n+2)');
            lis.forEach(li => {
                li.addEventListener('click', () => {
                    lis.forEach(li => {
                        li.classList.remove('selected');
                    });
                    li.classList.add('selected');
                });
            });
            this.messages = [];
        },
        editQuestion(question) {
            question.editable = true;
            const actions = event.target.parentNode;
            actions.children[0].style.display = 'none'; // hide edit icon
            actions.children[1].style.display = 'none'; // hide delete icon
            actions.children[2].style.display = 'inline-block'; // show check icon
        },
        saveQuestion(question) {
            question.editable = false;
            const actions = event.target.parentNode;
            actions.children[0].style.display = 'inline-block'; // show edit icon
            actions.children[1].style.display = 'inline-block'; // show delete icon
            actions.children[2].style.display = 'none'; // hide check icon
        },
        removeQuestion(id) {
            const index = this.questions.findIndex(q => q.id === id);
            if (index >= 0) {
                this.questions.splice(index, 1);
            }
        },
        // 点击“换一批”按钮，随机生成新的模板卡片列表
        refreshTemplate() {
            this.templateCards = [];
            while (this.templateCards.length < 6) {
                const card = this.messagesTemplate[Math.floor(Math.random() * this.messagesTemplate.length)];
                if (!this.templateCards.includes(card)) {
                    this.templateCards.push(card);
                }
            }
        },
        handleCardClick(card) {
            this.newMessage = card;
        },
        // 发送问题
        sendMessage() {
            if (!this.newMessage) {
                return;
            }
            const newId = this.messages.length + 1;
            const newMessage = {
                id: newId,
                text: this.newMessage,
            };
            this.messages.push(newMessage);
            this.scrollToBottom();
            this.sendOpenAIRequest(this.newMessage);
        },
        // 发送api接口
        async sendOpenAIRequest(question) {
            const token = 'Bearer ' + this.apiToken;
            console.info(token);
            const body = {
                model: "gpt-3.5-turbo",
                messages: [{ role: "user", "content": question }],
                temperature: 0.7
            }
            const response = await axios.post('https://api.openai.com/v1/chat/completions', body, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
            });
            this.messages.push({
                id: Date.now(),
                text: response.data.choices[0].message.content.replace(
                    /(\r\n|\n|\r)/g,
                    "<br>"
                ),
                time: new Date().toLocaleString(),
            });
            this.newMessage = '';
        },
        // 滚动到最底部
        scrollToBottom() {
            this.$nextTick(() => {
                const chatMessages = document.getElementById("chat-messages");
                const { scrollTop, clientHeight, scrollHeight } = chatMessages;
                const scroll = scrollHeight - clientHeight;
                chatMessages.scrollTop = scroll;
            });
        },
        generateUUID() {
            let d = new Date().getTime();
            let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        }
    }
}
</script>
  
<style lang="scss" scoped>
@import "./index.scss";
</style>

<style scoped>
.chatgpt-dialog {
    position: fixed;
    bottom: 140px;
    right: 50px;
    width: 800px;
    height: 600px;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
    z-index: 9999;
    display: flex;
    flex-direction: column;
}

.chatgpt-dialog-header {
    height: 50px;
    background-color: #20a6f4;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 10px;
}

.chatgpt-dialog-title {
    color: white;
    font-weight: bold;
    font-size: 20px;
    text-align: center;
    flex-grow: 1;
}

.chatgpt-dialog-close {
    color: white;
    margin-right: 10px;
    cursor: pointer;
    font-size: 16px;
}

.chatgpt-dialog-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    background-color: #fff;
}

.chatgpt-dialog-session {
    width: 30%;
    text-align: center;
    border-right: 1px solid #ccc;
    padding: 10px;
    display: flex;
    justify-content: center;
}

.chatgpt-dialog-session ul {
    margin: 0;
    padding: 0;
    list-style: none;
    width: 100%;
    max-height: 550px;
    overflow-y: auto;
}

.chatgpt-dialog-session li {
    height: 50px;
    margin-bottom: 10px;
    padding: 10px;
    font-size: 14px;
    border-radius: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
}

.chatgpt-dialog-session-new-session {
    border: 1px dashed #ccc;
    border-radius: 5px;
}

.chatgpt-dialog-session-new-session:hover {
    border-color: #20a6f4;
    color: #20a6f4;
}

.chatgpt-dialog-session li:nth-child(n+2) {
    border: 1px solid #ccc;
    border-radius: 5px;
}

.chatgpt-dialog-session li:nth-child(n+2):hover {
    background-color: #f5f5f5;
}

.chatgpt-dialog-session li:nth-child(n+2).active {
    background-color: #20a6f4;
    color: #fff;
    border-color: #20a6f4;
}

.chatgpt-dialog-session li.selected {
    border-color: #20a6f4;
    color: #20a6f4;
    background-color: #f5f5f5;
}

.chatgpt-dialog-session-question {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    padding: 10px;
    border-radius: 5px;
    cursor: pointer;
}

.chatgpt-dialog-session-question i {
    margin-right: 10px;
}

.chatgpt-dialog-session-question-content {
    flex: 1;
}

.chatgpt-dialog-session-question-actions {
    display: flex;
}

.chatgpt-dialog-session-question-actions i {
    margin-left: 10px;
    cursor: pointer;
}

.chatgpt-dialog-chat {
    width: 70%;
    height: 550px;
    display: flex;
    flex-direction: column;
}

.chatgpt-dialog-messages {
    flex: 1;
    height: 480px;
    overflow-y: scroll;
    padding: 10px;
    display: flex;
    flex-direction: column;
}

.chatgpt-dialog-message {
    display: flex;
    width: 100%;
    margin-bottom: 10px;
    justify-content: center;
    min-height: 50px;
}

.chatgpt-dialog-message:nth-child(even) {
    background-color: #fff;
}

.chatgpt-dialog-message:nth-child(odd) {
    background-color: #F7F7F8;
}

.chatgpt-dialog-message-content {
    width: 425px;
    overflow-y: auto;
    display: flex;
    align-items: center;
}

.chatgpt-dialog-avatar {
    margin-right: 20px;
}

.chatgpt-dialog-message-text {
    font-size: 14px;
    margin: 5px 0;
}

/* 模版内容样式 */
.chatgpt-dialog-template {
    padding: 20px;
}

/* 标题和副标题居中 */
.chatgpt-dialog-template-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
}

.chatgpt-dialog-template-title,
.chatgpt-dialog-template-subtitle {
    text-align: center;
}

/* 标题样式 */
.chatgpt-dialog-template-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
}

/* 副标题样式 */
.chatgpt-dialog-template-subtitle {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
}

.chatgpt-dialog-template-subtitle span {
    font-size: 14px;
}

/* 换一批按钮样式 */
.chatgpt-dialog-template-refresh {
    margin-left: 5px;
    color: #409EFF;
    cursor: pointer;
}

/* 换一批图标样式 */
.chatgpt-dialog-template-refresh-icon {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
}

/* 卡片列表样式 */
.chatgpt-dialog-template-cards {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
}

/* 卡片样式 */
.chatgpt-dialog-template-card {
    width: calc(50% - 10px);
    margin-bottom: 20px;
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 15px 10px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, .15);
    height: 70px;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
}

/* 卡片内容样式 */
.chatgpt-dialog-template-card-text {
    font-size: 16px;
    color: #5B5B5B;
    line-height: 20px;
    height: 40px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
    margin: auto 0;
}

.chatgpt-dialog-input {
    height: 70px;
    display: flex;
    align-items: center;
    padding: 10px;
    border-top: 1px solid #ccc;
}

.chatgpt-dialog-input input {
    flex: 1;
    padding: 5px;
    border: none;
    border-radius: 5px;
    margin-right: 10px;
    font-size: 14px;
}

.chatgpt-dialog-send {
    background-color: #20a6f4;
    color: #fff;
    border: none;
    border-radius: 5px;
    padding: 5px 10px;
    font-size: 16px;
    cursor: pointer;
}
</style>