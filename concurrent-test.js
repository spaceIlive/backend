// 진짜 동시 100명 접속 테스트
async function realConcurrentTest() {
    const baseUrl = 'http://localhost:8080';
    const concurrentUsers = 100;
    
    console.log(`🚀 ${concurrentUsers}명 동시 접속 테스트 시작...`);
    const startTime = Date.now();
    
    // 100개의 요청을 동시에 생성
    const promises = [];
    for (let i = 1; i <= concurrentUsers; i++) {
        const userId = Math.floor(Math.random() * 100) + 1; // 1-100 중 랜덤
        const promise = fetch(`${baseUrl}/api/folders/structure/${userId}`)
            .then(response => ({
                userId,
                status: response.status,
                responseTime: Date.now() - startTime,
                success: response.ok
            }))
            .catch(error => ({
                userId,
                status: 'ERROR',
                responseTime: Date.now() - startTime,
                success: false,
                error: error.message
            }));
        promises.push(promise);
    }
    
    // 모든 요청을 동시에 실행하고 결과 대기
    const results = await Promise.all(promises);
    const endTime = Date.now();
    
    // 결과 분석
    const successCount = results.filter(r => r.success).length;
    const errorRate = ((concurrentUsers - successCount) / concurrentUsers * 100).toFixed(2);
    const avgResponseTime = results.reduce((sum, r) => sum + r.responseTime, 0) / results.length;
    const maxResponseTime = Math.max(...results.map(r => r.responseTime));
    const minResponseTime = Math.min(...results.map(r => r.responseTime));
    
    console.log('📊 테스트 결과:');
    console.log(`⏱️ 총 소요 시간: ${endTime - startTime}ms`);
    console.log(`✅ 성공: ${successCount}/${concurrentUsers}`);
    console.log(`❌ 에러율: ${errorRate}%`);
    console.log(`📈 평균 응답시간: ${avgResponseTime.toFixed(2)}ms`);
    console.log(`⚡ 최소 응답시간: ${minResponseTime}ms`);
    console.log(`🐌 최대 응답시간: ${maxResponseTime}ms`);
    console.log(`🎯 목표 달성 (평균<1초, 에러<1%): ${avgResponseTime < 1000 && errorRate < 1 ? '✅ 성공!' : '❌ 실패'}`);
    
    // 상세 통계
    const responseTimes = results.map(r => r.responseTime).sort((a, b) => a - b);
    const p95 = responseTimes[Math.floor(responseTimes.length * 0.95)];
    const p99 = responseTimes[Math.floor(responseTimes.length * 0.99)];
    
    console.log('📊 상세 통계:');
    console.log(`📈 95% 응답시간: ${p95}ms`);
    console.log(`📈 99% 응답시간: ${p99}ms`);
    console.log(`🚀 초당 처리량 (TPS): ${(1000 * concurrentUsers / (endTime - startTime)).toFixed(2)} req/sec`);
    
    return results;
}

// 단계적 증가 테스트 (10명 → 50명 → 100명)
async function stepLoadTest() {
    const steps = [10, 50, 100];
    
    for (const userCount of steps) {
        console.log(`\n🔄 ${userCount}명 동시 접속 테스트 시작...`);
        
        const concurrentUsers = userCount;
        const baseUrl = 'http://localhost:8080';
        const startTime = Date.now();
        
        const promises = [];
        for (let i = 1; i <= concurrentUsers; i++) {
            const userId = Math.floor(Math.random() * 100) + 1;
            const promise = fetch(`${baseUrl}/api/folders/structure/${userId}`)
                .then(response => ({ success: response.ok, responseTime: Date.now() - startTime }))
                .catch(() => ({ success: false, responseTime: Date.now() - startTime }));
            promises.push(promise);
        }
        
        const results = await Promise.all(promises);
        const successCount = results.filter(r => r.success).length;
        const avgTime = results.reduce((sum, r) => sum + r.responseTime, 0) / results.length;
        
        console.log(`${userCount}명 결과: 성공 ${successCount}/${userCount}, 평균 ${avgTime.toFixed(2)}ms`);
        
        // 다음 테스트 전 잠시 대기
        await new Promise(resolve => setTimeout(resolve, 2000));
    }
}

// 지속적 부하 테스트 (5분간)
async function sustainedLoadTest() {
    const duration = 5 * 60 * 1000; // 5분
    const interval = 1000; // 1초마다
    const concurrentUsers = 100;
    const baseUrl = 'http://localhost:8080';
    
    console.log('🔥 5분간 지속적 부하 테스트 시작...');
    const startTime = Date.now();
    const results = [];
    
    while (Date.now() - startTime < duration) {
        const roundStart = Date.now();
        
        const promises = [];
        for (let i = 1; i <= concurrentUsers; i++) {
            const userId = Math.floor(Math.random() * 100) + 1;
            const promise = fetch(`${baseUrl}/api/folders/structure/${userId}`)
                .then(response => ({ success: response.ok, time: Date.now() }))
                .catch(() => ({ success: false, time: Date.now() }));
            promises.push(promise);
        }
        
        const roundResults = await Promise.all(promises);
        results.push(...roundResults);
        
        const elapsed = Date.now() - startTime;
        const successRate = (results.filter(r => r.success).length / results.length * 100).toFixed(1);
        console.log(`${Math.floor(elapsed/1000)}초 경과: 성공률 ${successRate}%`);
        
        // 다음 라운드까지 대기
        const waitTime = interval - (Date.now() - roundStart);
        if (waitTime > 0) {
            await new Promise(resolve => setTimeout(resolve, waitTime));
        }
    }
    
    console.log('🏁 지속적 부하 테스트 완료!');
}

// 실행 함수들
console.log('🎯 성능 테스트 스크립트 로드 완료!');
console.log('📝 사용 방법:');
console.log('  realConcurrentTest()    - 100명 동시접속 테스트');
console.log('  stepLoadTest()          - 단계적 부하 테스트');
console.log('  sustainedLoadTest()     - 5분간 지속적 부하 테스트'); 