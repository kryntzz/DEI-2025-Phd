<template>
  <div class="min-h-screen bg-white p-6 flex flex-col items-center justify-center">
    <!-- IST Logo -->
    <div class="mb-8">
      <!-- Option 1: Using a local image file -->
      <img 
        src="./assets/Técnico Lisboa_idQl4ai-Ef_0.png" 
        alt="Instituto Superior Técnico Logo" 
        class="w-32"
        onerror="this.style.display='none'; this.nextElementSibling.style.display='block';"
      />
      <!-- Fallback if image doesn't load -->
      <div class="hidden text-2xl font-bold text-red-700">
        Instituto Superior Técnico
      </div>
    </div>
    
    <!-- Main Content -->
    <div class="max-w-2xl text-center">
      <h1 class="text-3xl font-bold text-gray-900 mb-4">
        {{ appName }}
      </h1>
      
      <div class="bg-gray-50 p-6 rounded-lg shadow-sm">
        <p class="text-lg text-gray-700 mb-4">
          Instituto Superior Técnico (IST) is Portugal's largest and most prestigious school of engineering, science and technology. 
          Founded in 1911, IST is part of the University of Lisbon.
        </p>
        
        <p class="text-lg text-gray-700">
          Our PhD program offers advanced research opportunities across multiple engineering disciplines, 
          supported by world-class faculty and state-of-the-art facilities. 
          Join us to contribute to cutting-edge research and innovation.
        </p>
      </div>
      
      <!-- Simple CTA -->
      <a 
        href="#" 
        class="inline-block mt-8 px-6 py-3 bg-red-700 text-white font-medium rounded-md hover:bg-red-800"
      >
        Learn More
      </a>
    </div>
    
    <!-- Footer -->
    <div class="mt-12 text-center text-sm text-gray-500">
      © {{ new Date().getFullYear() }} Instituto Superior Técnico
    </div>
  </div>
</template>

<script setup lang="ts">
const appName = import.meta.env.VITE_NAME || "PhD Program";
</script>