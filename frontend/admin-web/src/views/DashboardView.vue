<template>
  <div>
    <h2>관리자 대시보드</h2>
    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px">
      <div style="padding:20px;background:#f8f9fa;border-radius:8px">
        <h3>게시글</h3>
        <router-link to="/products">관리하기</router-link>
      </div>
      <div style="padding:20px;background:#f8f9fa;border-radius:8px">
        <h3>사용자</h3>
        <router-link to="/users">관리하기</router-link>
      </div>
    </div>
  </div>
</template>
