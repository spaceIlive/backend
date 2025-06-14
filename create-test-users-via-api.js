// API를 통해 테스트 사용자 50명 생성하는 스크립트
// Node.js 환경에서 실행하거나 브라우저 개발자 도구에서 실행

async function createTestUsers() {
    const baseUrl = 'http://localhost:8080';
    const results = [];
    
    console.log('🚀 테스트 사용자 100명 생성 시작...');
    
    for (let i = 1; i <= 100; i++) {
        try {
            const userData = {
                username: `testuser${i}`,
                email: `test${i}@example.com`,
                password: 'password123'
            };
            
            const response = await fetch(`${baseUrl}/api/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            
            if (response.ok) {
                const result = await response.json();
                results.push(result);
                console.log(`✅ User ${i} 생성 완료: ID ${result.id}`);
            } else {
                const error = await response.text();
                console.error(`❌ User ${i} 생성 실패: ${error}`);
            }
            
            // API 부하 방지를 위한 딜레이
            await new Promise(resolve => setTimeout(resolve, 100));
            
        } catch (error) {
            console.error(`❌ User ${i} 생성 중 오류:`, error);
        }
    }
    
    console.log(`🎉 완료! 총 ${results.length}명의 사용자가 생성되었습니다.`);
    
    // 생성된 사용자 ID 목록 출력
    console.log('생성된 사용자 ID 목록:', results.map(r => r.id));
    
    return results;
}

// 복잡한 파일 구조 생성 (처음 30명용)
async function createComplexData(users) {
    const baseUrl = 'http://localhost:8080';
    
    console.log('🏗️ 복잡한 파일 구조 생성 중 (30명)...');
    
    for (const user of users.slice(0, 30)) { // 처음 30명
        try {
            const rootFolderId = user.id; // 루트 폴더 ID
            const createdFolders = [];
            
            // 1단계: 메인 카테고리 폴더들 생성 (5개)
            const mainCategories = ['프로젝트', '연습작품', '완성작품', '아이디어스케치', '자료모음'];
            
            for (const categoryName of mainCategories) {
                const folderResponse = await fetch(`${baseUrl}/api/folders`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: new URLSearchParams({
                        userId: user.id,
                        name: categoryName,
                        parentId: rootFolderId
                    })
                });
                
                if (folderResponse.ok) {
                    const folder = await folderResponse.json();
                    createdFolders.push(folder);
                    console.log(`📁 User ${user.id}: ${categoryName} 폴더 생성`);
                    
                    // 2단계: 각 메인 카테고리에 하위 폴더 2-3개씩 생성
                    const subFolderCount = Math.floor(Math.random() * 2) + 2; // 2-3개
                    for (let k = 1; k <= subFolderCount; k++) {
                        const subFolderResponse = await fetch(`${baseUrl}/api/folders`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: new URLSearchParams({
                                userId: user.id,
                                name: `${categoryName}_하위${k}`,
                                parentId: folder.folder_id
                            })
                        });
                        
                        if (subFolderResponse.ok) {
                            const subFolder = await subFolderResponse.json();
                            createdFolders.push(subFolder);
                        }
                    }
                }
                
                await new Promise(resolve => setTimeout(resolve, 100));
            }
            
            // 3단계: 각 폴더에 스케치 생성 (폴더당 3-8개)
            for (const folder of createdFolders) {
                const sketchCount = Math.floor(Math.random() * 6) + 3; // 3-8개
                
                for (let j = 1; j <= sketchCount; j++) {
                    const drawingResponse = await fetch(`${baseUrl}/api/drawings`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: new URLSearchParams({
                            title: `${folder.name}_작품${j}`,
                            content: `complex_sketch_data_user${user.id}_folder${folder.folder_id}_${j}`,
                            folderId: folder.folder_id
                        })
                    });
                    
                    if (drawingResponse.ok) {
                        console.log(`✏️ User ${user.id}: ${folder.name}에 작품${j} 생성`);
                    }
                    
                    await new Promise(resolve => setTimeout(resolve, 50));
                }
            }
            
        } catch (error) {
            console.error(`User ${user.id} 복잡한 데이터 생성 실패:`, error);
        }
        
        await new Promise(resolve => setTimeout(resolve, 300));
    }
}

// 간단한 파일 구조 생성 (나머지 70명용)
async function createSimpleData(users) {
    const baseUrl = 'http://localhost:8080';
    
    console.log('📄 간단한 파일 구조 생성 중 (70명)...');
    
    for (const user of users.slice(30)) { // 31번째부터 100번째까지
        try {
            const rootFolderId = user.id; // 루트 폴더 ID
            
            // 1단계: 간단한 폴더 1-2개만 생성
            const folderNames = ['내 작품', '연습'];
            const folderCount = Math.floor(Math.random() * 2) + 1; // 1-2개
            
            for (let f = 0; f < folderCount; f++) {
                const folderResponse = await fetch(`${baseUrl}/api/folders`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: new URLSearchParams({
                        userId: user.id,
                        name: folderNames[f],
                        parentId: rootFolderId
                    })
                });
                
                if (folderResponse.ok) {
                    const folder = await folderResponse.json();
                    
                    // 2단계: 각 폴더에 스케치 1-3개만 생성
                    const sketchCount = Math.floor(Math.random() * 3) + 1; // 1-3개
                    
                    for (let j = 1; j <= sketchCount; j++) {
                        const drawingResponse = await fetch(`${baseUrl}/api/drawings`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: new URLSearchParams({
                                title: `작품${j}`,
                                content: `simple_sketch_data_user${user.id}_${j}`,
                                folderId: folder.folder_id
                            })
                        });
                        
                        if (drawingResponse.ok) {
                            console.log(`✏️ User ${user.id}: 간단한 작품${j} 생성`);
                        }
                    }
                }
            }
            
            // 3단계: 루트 폴더에도 직접 스케치 1-2개 생성
            const rootSketchCount = Math.floor(Math.random() * 2) + 1; // 1-2개
            for (let r = 1; r <= rootSketchCount; r++) {
                const drawingResponse = await fetch(`${baseUrl}/api/drawings`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: new URLSearchParams({
                        title: `간단작품${r}`,
                        content: `root_sketch_data_user${user.id}_${r}`,
                        folderId: rootFolderId
                    })
                });
                
                if (drawingResponse.ok) {
                    console.log(`📝 User ${user.id}: 루트 폴더에 간단작품${r} 생성`);
                }
            }
            
        } catch (error) {
            console.error(`User ${user.id} 간단한 데이터 생성 실패:`, error);
        }
        
        await new Promise(resolve => setTimeout(resolve, 150));
    }
}

// 메인 실행 함수
async function main() {
    try {
        // 1. 사용자 100명 생성
        console.log('📊 단계 1: 사용자 생성 시작...');
        const users = await createTestUsers();
        
        if (users.length > 0) {
            // 2. 복잡한 파일 구조 생성 (처음 30명)
            console.log('📊 단계 2: 복잡한 파일 구조 생성 시작...');
            await createComplexData(users);
            
            // 3. 간단한 파일 구조 생성 (나머지 70명)
            console.log('📊 단계 3: 간단한 파일 구조 생성 시작...');
            await createSimpleData(users);
        }
        
        console.log('🎯 모든 테스트 데이터 생성 완료!');
        console.log(`✅ 총 100명의 사용자 생성`);
        console.log(`🏗️ 복잡한 구조: 30명 (폴더 5-15개, 스케치 15-50개)`);
        console.log(`📄 간단한 구조: 70명 (폴더 1-2개, 스케치 2-8개)`);
        
    } catch (error) {
        console.error('❌ 스크립트 실행 중 오류:', error);
    }
}

// 브라우저에서 실행하는 경우
if (typeof window !== 'undefined') {
    // 브라우저 환경
    console.log('브라우저에서 실행 중...');
    main();
} else {
    // Node.js 환경
    main();
}

// 개별 테스트용 함수들
window.createTestUsers = createTestUsers;
window.createComplexData = createComplexData;
window.createSimpleData = createSimpleData; 