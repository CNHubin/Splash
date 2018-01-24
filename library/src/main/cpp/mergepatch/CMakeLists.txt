


file(GLOB my_source_path  *.cpp  *.c)
add_library( hubinPatch
             SHARED
             ${my_source_path} )


find_library( log-lib
              log )

target_link_libraries( hubinPatch
                       ${log-lib}
                       )
