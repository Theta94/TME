package tme.model;

public final class XQueryDB {
    public static final String calculate_prioritization_coverage = "xquery version '1.0';\n" +
"\n" +
"(: Variabile passata all'applicativo Java - progetto :)\n" +
"declare variable $project external;\n" +
"(: Variabile passata all'applicativo Java - versione :)\n" +
"declare variable $version external;\n" +
"(: Variabile passata all'applicativo Java - tipo su cui è avvenuta la prioritizzazione :)\n" +
"declare variable $type external;\n" +
"(: Variabile passata all'applicativo Java - movimentazione :)\n" +
"declare variable $movimentation external;\n" +
"(: Riferimento alla collezione contenente tutti i file da aprire :)\n" +
"let $input_new_collection := collection(concat('/db/root/', $project, '/input/report_compact'))\n" +
"(: Riferimento alla collezione contenente tutte le prioritizzazioni :)\n" +
"let $prioritization_collection := collection(concat('/db/root/', $project, '/output/Prioritization/'))\n" +
"(: Riferimento alla collezione che conterrà il file risultante :)\n" +
"let $output_doc_check := doc(concat('/db/root/', $project, '/output/Coverage/', $version, '_', $type, '_', $movimentation, '.xml'))\n" +
"(: Se il documento di output non esiste, crealo :)\n" +
"let $create_output_doc := (\n" +
"    if (count($output_doc_check) = 0) then\n" +
"        xmldb:store(concat('/db/root/', $project, '/output/Coverage'), concat($version, '_', $type, '_', $movimentation, '.xml'), '<a/>')\n" +
"    else ()\n" +
")\n" +
"(: Apri il documento di output :)\n" +
"let $output_doc := doc(concat('/db/root/', $project, '/output/Coverage/', $version, '_', $type, '_', $movimentation, '.xml'))\n" +
"(: Recupera tutte le prioritizzazioni :)\n" +
"let $prioritization_docs := (\n" +
"    for $doc in $prioritization_collection where contains(replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2'), $version) and\n" +
"                                                 contains(replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2'), $type) and\n" +
"                                                 contains(replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2'), $movimentation)\n" +
"    return $doc\n" +
")\n" +
"(: Recupera tutti i file report appartenenti alla versione specificata :)\n" +
"let $report_docs := (\n" +
"    for $doc in $input_new_collection where contains(replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2'), $version)\n" +
"    return $doc\n" +
")\n" +
"(: Prepara il documento di output inserendo all'interno dei tag (uno per prioritizzazione) \n" +
" : che ospiteranno la copertura per quella prioritizzazione :)\n" +
"let $build_coverage_base := (\n" +
"    let $root := (\n" +
"        let $prepare := (\n" +
"            for $prio in $prioritization_docs\n" +
"            return \n" +
"                <Prioritization sort='{$prio//program/@sort}' type='{$prio//program/@type}' version='{$prio//program/@version}'/>\n" +
"        )\n" +
"    return <Coverage version='{$version}' type='{$type}' movimentation='{$movimentation}' >{$prepare}</Coverage>\n" +
"    )\n" +
"return xmldb:store(concat('/db/root/', $project, '/output/Coverage'), concat($version, '_', $type, '_', $movimentation, '.xml'), $root)\n" +
")\n" +
"(: Per ogni prioritizzazione esistente per quella versione, apri i file di report nell'ordine\n" +
" : specificato per la corrente prioritizzazione e, se un nodo sourcefile non esiste all'interno \n" +
" : del documento di output, crealo. Per ogni nodo line appartenente al corrente nodo sourcefile,\n" +
" : controlla se questo nodo esiste già all'interno del documento di output; se non esiste, inseriscilo\n" +
" : andando ad aggiungere un attributo pos che specifica la posizione nella prioritizzazione del test che\n" +
" : per primo ha coperto quella linea. :)\n" +
"for $prio in $prioritization_docs\n" +
"for $rep at $pos in $prio//parameters\n" +
"for $node in doc(concat('/db/root/', $project, '/input/report_compact/Test_', $rep/@num_test, '_', $version, '.xml'))//sourcefile\n" +
"return\n" +
"    let $x := (\n" +
"        if (count($output_doc//Prioritization[@sort = $prio//program/@sort]//sourcefile[@name = $node/@name]) = 0) then\n" +
"            update insert <sourcefile name='{$node/@name}'/> into $output_doc//Prioritization[@sort = $prio//program/@sort]\n" +
"        else ()\n" +
"    )\n" +
"    for $line in $node/line\n" +
"    return\n" +
"        if (count($output_doc//Prioritization[@sort = $prio//program/@sort]//sourcefile[@name = $node/@name]/line[@nr = $line/@nr]) = 0) then\n" +
"            update insert <line nr='{$line/@nr}' mi='{$line/@mi}' ci='{$line/@ci}' mb='{$line/@mb}' cb='{$line/@cb}' pos='{$pos}'/> into $output_doc//Prioritization[@sort = $prio//program/@sort]//sourcefile[@name = $node/@name]\n" +
"        else ()";
    
    public static final String calculate_report_compact = "xquery version '1.0';\n" +
"\n" +
"(: Variabile passata all'applicativo Java - progetto :)\n" +
"declare variable $project external;\n" +
"(: Riferimento alla collezione contenente tutti i file da aprire :)\n" +
"let $input_collection := collection(concat('/db/root/', $project, '/input/report'))\n" +
"(: Riferimento alla collezione che conterrà tutti i file risultanti :)\n" +
"let $output_collection := concat('/db/root/', $project, '/input/report_compact')\n" +
"(: Iterazione di tutti i file contenuti nella collezione :)\n" +
"for $doc in $input_collection\n" +
"(: Nodo output per questo file  :)\n" +
"let $output := (\n" +
"    (: Per il seguente file, scorri tutti i nodi sourcefile e ritorna un nodo che contiene tutti i sourcefile selezionando solo alcuni nodi line  :)\n" +
"    let $sourcefiles := (\n" +
"        for $sourcefile in $doc//sourcefile\n" +
"        (: Per ogni sourcefile, seleziona solo i figli line che hanno come attributo mi un valore uguale a 0 :)\n" +
"        let $line := (\n" +
"            for $l in $sourcefile/line where $l/@mi = 0\n" +
"            return <line nr='{$l/@nr}' mi='{$l/@mi}' ci='{$l/@ci}' mb='{$l/@mb}' cb='{$l/@cb}'/>\n" +
"        )\n" +
"        return <sourcefile name='{$sourcefile/@name}'>{$line}</sourcefile>\n" +
"    )\n" +
"    return <Covered name='{replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2')}'>{$sourcefiles}</Covered>\n" +
")\n" +
"(: Salva i nuovi sourcefile dentro un nuovo file :)\n" +
"let $filename := replace(fn:base-uri($doc), '^(.*/)(.*?\\.\\w+$)', '$2')\n" +
"return xmldb:store($output_collection, $filename, $output)";
}
