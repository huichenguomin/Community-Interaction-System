// new Vue({
//     el: '#app',
//     data:{
//             username: '',
//             password: ''
//     },
//     methods: {
//         loginIn:function (){
//             // axios.get("http://localhost/cis?username="+this.username+"&password="+this.password)
//             //     .then((res)=>{
//             //
//             //     })
//             axios.post('/cis',{
//                 username: this.username,
//                 password: this.password
//                 },{
//                 headers:{
//                     'Content-Type': 'application/json'
//                 }
//                 });
//         }
//     }
// });

function loginIn(){
    new Vue({
        el: '#app',
        data:{
                usn: '',
                pswd: ''
        },
        mounted(){
            axios.post('http://localhost/cis',{
                username: this.usn,
                password: this.pswd
            },{
                headers:{
                    'Content-Type': 'application/json'
                 }
            })
        }
    })
}